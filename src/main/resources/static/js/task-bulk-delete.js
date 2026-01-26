
async function deleteSingleTask(id) {
    if (!confirm('Move this task to the bin?')) return;

    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    try {
        const response = await fetch(`/api/tasks/delete/${id}`, {
            method: 'DELETE',
            headers: {
                [header]: token,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            window.location.reload();s
        } else {
            const errorText = await response.text();
            alert("Delete failed: " + errorText);
        }
    } catch (error) {
        console.error("Connection Error:", error);
    }
}


document.addEventListener('DOMContentLoaded', () => {
    const selectAllCheckbox = document.getElementById('selectAllTasks');
    const bulkActions = document.getElementById('bulkActions');
    const bulkDeleteBtn = document.getElementById('bulkDeleteBtn');

    function getCsrfHeaders() {
        const tokenMeta = document.querySelector('meta[name="_csrf"]');
        const headerMeta = document.querySelector('meta[name="_csrf_header"]');
        if (!tokenMeta || !headerMeta) return {};
        return { [headerMeta.content]: tokenMeta.content };
    }

    function getSelectedTaskIds() {
        return Array.from(document.querySelectorAll('.task-select:checked'))
            .map(cb => cb.value);
    }

    function updateBulkActionsVisibility() {
        const hasSelection = getSelectedTaskIds().length > 0;
        if(bulkActions) bulkActions.classList.toggle('hidden', !hasSelection);
    }

    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', () => {
            document.querySelectorAll('.task-select').forEach(cb => {
                cb.checked = selectAllCheckbox.checked;
            });
            updateBulkActionsVisibility();
        });
    }

    document.addEventListener('change', event => {
        if (!event.target.classList.contains('task-select')) return;
        const allCheckboxes = document.querySelectorAll('.task-select');
        const checked = document.querySelectorAll('.task-select:checked');

        if (selectAllCheckbox) {
            selectAllCheckbox.checked = allCheckboxes.length === checked.length;
        }
        updateBulkActionsVisibility();
    });

    if (bulkDeleteBtn) {
        bulkDeleteBtn.addEventListener('click', () => {
            const taskIds = getSelectedTaskIds();
            if (taskIds.length === 0) return;

            if (!confirm(`Move ${taskIds.length} selected task(s) to bin?`)) return;

            fetch('/api/tasks/bulk-delete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...getCsrfHeaders()
                },
                body: JSON.stringify({ taskIds: taskIds })
            })
            .then(res => {
                if (res.ok) window.location.reload();
                else throw new Error('Bulk delete failed');
            })
            .catch(err => alert('Error: ' + err.message));
        });
    }
});