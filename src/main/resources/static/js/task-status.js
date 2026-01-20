document.addEventListener('DOMContentLoaded', () => {


    const UNDO_WINDOW_MS = 15000;
    const UNDO_STORAGE_KEY = 'taskUndoState';

    function getCsrfHeaders() {
        const tokenMeta = document.querySelector('meta[name="_csrf"]');
        const headerMeta = document.querySelector('meta[name="_csrf_header"]');
        if (!tokenMeta || !headerMeta) return {};
        return { [headerMeta.content]: tokenMeta.content };
    }

    function applyStatusUI(row, status) {
        if (!row) return;

        const checkbox = row.querySelector('.status-checkbox');
        const markDoneBtn = row.querySelector('.mark-done-btn');
        const completedLabel = row.querySelector('.completed-label');
        const editBtn = row.querySelector('.btn-edit');

        if (checkbox) {
            checkbox.checked = status === 'DONE';
            checkbox.disabled = status === 'DONE';
        }

        row.classList.toggle('completed-row', status === 'DONE');
        row.classList.remove('overdue-row');

        if (markDoneBtn && status === 'DONE') markDoneBtn.remove();
        if (completedLabel)
            completedLabel.style.display = status === 'DONE' ? 'inline-flex' : 'none';
        if (editBtn)
            editBtn.style.display = status === 'DONE' ? 'none' : '';
    }

    function updateTaskStatus(taskId, status) {
        return fetch(`/api/tasks/${taskId}/status?status=${status}`, {
            method: 'PATCH',
            headers: getCsrfHeaders()
        }).then(res => {
            if (!res.ok) throw new Error('Status update failed');
        });
    }

    function persistUndo(taskId) {
        const expiresAt = Date.now() + UNDO_WINDOW_MS;
        localStorage.setItem(UNDO_STORAGE_KEY, JSON.stringify({ taskId, expiresAt }));
        return expiresAt;
    }

    function clearUndoState() {
        localStorage.removeItem(UNDO_STORAGE_KEY);
        disableUndoUI();
    }

    function getUndoState() {
        const raw = localStorage.getItem(UNDO_STORAGE_KEY);
        return raw ? JSON.parse(raw) : null;
    }

    const undoBar = document.getElementById('undoBar');
    const undoBtn = document.getElementById('undoBtn');

    function enableUndoUI() {
        if (!undoBar || !undoBtn) return;
        undoBar.classList.remove('hidden');
        undoBtn.disabled = false;
    }

    function disableUndoUI() {
        if (!undoBar || !undoBtn) return;
        undoBar.classList.remove('hidden');
        undoBtn.disabled = true;
    }

    function startUndoExpiryTimer(ms) {
        setTimeout(() => {
            clearUndoState();
        }, ms);
    }

    function restoreUndoOnLoad() {
        const state = getUndoState();
        if (!state) {
            disableUndoUI();
            return;
        }

        const remaining = state.expiresAt - Date.now();
        if (remaining <= 0) {
            clearUndoState();
            return;
        }

        enableUndoUI();
        startUndoExpiryTimer(remaining);

        undoBtn.onclick = () => handleUndo(state.taskId);
    }


    function handleUndo(taskId) {
        updateTaskStatus(taskId, 'PENDING')
            .then(() => location.reload())
            .finally(clearUndoState);
    }


    function completeTask(taskId, row) {
        updateTaskStatus(taskId, 'DONE')
            .then(() => {
                applyStatusUI(row, 'DONE');
                const expiresAt = persistUndo(taskId);
                enableUndoUI();
                startUndoExpiryTimer(expiresAt - Date.now());
                undoBtn.onclick = () => handleUndo(taskId);
            })
            .catch(() => alert('Could not update task status'));
    }


    document.addEventListener('change', e => {
        if (!e.target.classList.contains('status-checkbox')) return;
        const checkbox = e.target;
        const row = checkbox.closest('tr');
        completeTask(checkbox.dataset.id, row);
    });


    document.addEventListener('click', e => {
        const btn = e.target.closest('.mark-done-btn');
        if (!btn) return;
        completeTask(btn.dataset.id, btn.closest('tr'));
    });


    restoreUndoOnLoad();

});
