let undoTimeout = null;
let undoAction = null;

function showUndoToast(message, action, duration = 5000) {
    const toast = document.getElementById("undo-toast");
    const messageEl = document.getElementById("undo-message");
    const undoBtn = document.getElementById("undo-btn");

    messageEl.textContent = message;
    toast.classList.remove("hidden");

    undoAction = action;

    undoBtn.onclick = () => {
        if (undoAction) {
            undoAction();
        }
        hideUndoToast();
    };

    if (undoTimeout) {
        clearTimeout(undoTimeout);
    }

    undoTimeout = setTimeout(() => {
        hideUndoToast();
    }, duration);
}

function hideUndoToast() {
    const toast = document.getElementById("undo-toast");
    toast.classList.add("hidden");
    undoAction = null;
}
