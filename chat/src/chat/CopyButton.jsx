import React from "react";
import 'font-awesome/css/font-awesome.min.css'
import './CopyButton.css';

export default function CopyButton({ children }) {
    const [copyOk, setCopyOk] = React.useState(false);

    const iconColor = copyOk ? '#0af20a' : '#ddd';
    const icon = navigator.clipboard ? (copyOk ? 'fa-check-square' : 'fa-clone') : 'fa-fw';

    const handleClick = (e) => {
        // clipboard requires a secure origin — either HTTPS or localhost
        if (navigator.clipboard) {
            navigator.clipboard.writeText(children.props.children);

            setCopyOk(true);
            setTimeout(() => {
                setCopyOk(false);
            }, 500);
        }
    }

    return (
        <div className="copy-btn">
            <i className={`fa ${icon}`} onClick={handleClick} style={{color: iconColor}} />
        </div>
    )
}
