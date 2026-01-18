function getCsrfHeaders() {
    const tokenMeta = document.querySelector('meta[name="_csrf"]');
    const headerMeta = document.querySelector('meta[name="_csrf_header"]');

    if (!tokenMeta || !headerMeta) {
        return {};
    }

    return {
        [headerMeta.content]: tokenMeta.content
    };
}

function updateTaskStatus(taskId, status, row) {
    return fetch(`/api/tasks/${taskId}/status?status=${status}`, {
        method: 'PATCH',
        headers: getCsrfHeaders()
    }).then(response => {
        if (!response.ok) {
            throw new Error('Request failed');
        }

        // ===== UI UPDATE =====
        const checkbox = row.querySelector('.status-checkbox');
        if (checkbox) {
            checkbox.checked = (status === 'DONE');
            checkbox.disabled = (status === 'DONE');
        }

        row.classList.toggle('completed-row', status === 'DONE');
        row.classList.remove('overdue-row');

        const markDoneBtn = row.querySelector('.mark-done-btn');
        if (markDoneBtn && status === 'DONE') {
            markDoneBtn.remove();
        }

        const completedLabel = row.querySelector('.completed-label');
        if (completedLabel) {
            completedLabel.style.display = (status === 'DONE') ? 'inline-flex' : 'none';
        }
    });
}

/* =====================
   Checkbox handler
===================== */
document.addEventListener('change', function (event) {
    if (!event.target.classList.contains('status-checkbox')) return;

    const checkbox = event.target;
    const taskId = checkbox.dataset.id;
    const row = checkbox.closest('tr');

    updateTaskStatus(taskId, 'DONE', row).catch(() => {
        checkbox.checked = false;
        alert('Could not update task status');
    });
});

/* =====================
   Button handler
===================== */
document.addEventListener('click', function (event) {
    const button = event.target.closest('.mark-done-btn');
    if (!button) return;

    const taskId = button.dataset.id;
    const row = button.closest('tr');

    updateTaskStatus(taskId, 'DONE', row).catch(() => {
        alert('Could not update task status');
    });
});
