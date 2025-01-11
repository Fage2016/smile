/*
 * Copyright (c) 2010-2025 Haifeng Li. All rights reserved.
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
package smile.validation;

import java.io.Serial;
import java.io.Serializable;

/**
 * A bag of random selected samples.
 *
 * @param samples the random samples.
 * @param oob the out of bag samples.
 * @author Haifeng Li
 */
public record Bag(int[] samples, int[] oob) implements Serializable {
    @Serial
    private static final long serialVersionUID = 3L;
}
