function setColorBasedOnScore() {
    const rows = document.querySelectorAll("#methodTable tbody tr");

    rows.forEach(row => {
        const miScore = parseInt(row.cells[0].textContent);
        if (miScore <= 10) {
            row.style.backgroundColor = "#ffcccc"; // Light red
        } else if (miScore <= 20) {
            row.style.backgroundColor = "#ffcc99"; // Light orange
        } else {
            row.style.backgroundColor = "#ccffcc"; // Light green
        }
    });
}

// Call the function after the page is loaded
window.onload = setColorBasedOnScore;

document.addEventListener('DOMContentLoaded', function () {
    const codeContainers = document.querySelectorAll('.code-container');

    codeContainers.forEach(container => {
        container.addEventListener('click', function () {
            if (this.style.maxHeight === 'none') {
                this.style.maxHeight = '160px';
            } else {
                this.style.maxHeight = 'none';
            }
        });
    });
});

var paragraphs = document.querySelectorAll(".copyText");
paragraphs.forEach(function(paragraph) {
    paragraph.onclick = function() {
        var range = document.createRange();
        range.selectNode(paragraph);

        window.getSelection().removeAllRanges();
        window.getSelection().addRange(range);

        document.execCommand("copy");
        window.getSelection().removeAllRanges();
    };
});
