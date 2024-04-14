/*
 * Copyright (c) 2010-2024 Haifeng Li. All rights reserved.
 *
 * Smile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Smile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Smile.  If not, see <https://www.gnu.org/licenses/>.
 */
package smile.vision;

import smile.deep.activation.SiLU;
import smile.deep.activation.Sigmoid;
import smile.deep.layer.BatchNorm2dLayer;
import smile.deep.layer.Layer;
import smile.deep.layer.LayerBlock;
import smile.deep.layer.SequentialBlock;
import smile.deep.tensor.Tensor;

/**
 * Mobile inverted bottleneck convolution.
 *
 * MBConv = expansion-conv1x1 + depthwise-conv3x3 + SENet + conv1x1 + add
 *
 * @author Haifeng Li
 */
public class MBConv extends LayerBlock {
    private final SequentialBlock block = new SequentialBlock();
    private final StochasticDepth stochasticDepth;
    private final boolean useResidual;

    /**
     * Constructor.
     * @param config block configuration.
     * @param stochasticDepthProb the probability of the input to be zeroed
     *                           in stochastic depth layer.
     */
    public MBConv(MBConvConfig config, double stochasticDepthProb) {
        super("MBConv");
        int stride = config.stride();
        if (stride < 1 || stride > 2) {
            throw new IllegalArgumentException("Illegal stride value: " + stride);
        }

        // expand
        int expandedChannels = MBConvConfig.adjustChannels(config.inputChannels(), config.expandRatio());
        if (expandedChannels != config.inputChannels()) {
            Conv2dNormActivation expand = new Conv2dNormActivation(
                            Layer.conv2d(config.inputChannels(), expandedChannels, 1, 1, -1, 1, 1, false, "zeros"),
                            new BatchNorm2dLayer(expandedChannels),
                            new SiLU(true));
            block.add(expand);
        }

        // depthwise
        Conv2dNormActivation depthwise = new Conv2dNormActivation(
                Layer.conv2d(expandedChannels, expandedChannels, config.kernel(), config.stride(), -1, 1, expandedChannels, false, "zeros"),
                new BatchNorm2dLayer(expandedChannels),
                new SiLU(true));
        block.add(depthwise);

        // squeeze and excitation
        int squeezeChannels = Math.max(1, config.inputChannels() / 4);
        SqueezeExcitation se = new SqueezeExcitation(expandedChannels, squeezeChannels, new SiLU(true), new Sigmoid(true));
        block.add(se);

        // project
        Conv2dNormActivation project = new Conv2dNormActivation(
                Layer.conv2d(expandedChannels, config.outputChannels(), 1, 1, -1, 1, 1, false, "zeros"),
                new BatchNorm2dLayer(config.outputChannels()), null);
        block.add(project);

        useResidual = stride == 1 && config.inputChannels() == config.outputChannels();
        stochasticDepth = new StochasticDepth(stochasticDepthProb, "row");

        add("block", block);
        add("stochastic_depth", stochasticDepth);
    }

    @Override
    public Tensor forward(Tensor input) {
        Tensor output = block.forward(input);
        if (useResidual) {
            output = stochasticDepth.forward(output);
            output.add_(input);
        }
        return output;
    }
}