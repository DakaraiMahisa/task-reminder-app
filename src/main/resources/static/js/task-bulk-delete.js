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
        bulkActions.classList.toggle('hidden', !hasSelection);
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
            selectAllCheckbox.checked =
                allCheckboxes.length === checked.length;
        }

        updateBulkActionsVisibility();
    });


    if (bulkDeleteBtn) {
        bulkDeleteBtn.addEventListener('click', () => {
            const taskIds = getSelectedTaskIds();
            if (taskIds.length === 0) return;

            if (!confirm(`Delete ${taskIds.length} selected task(s)?`)) {
                return;
            }

            fetch('/api/tasks/bulk-delete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    ...getCsrfHeaders()
                },
                body: JSON.stringify({
                    taskIds: taskIds
                })
            })
            .then(res => {
                if (!res.ok) throw new Error('Bulk delete failed');
            })
            .then(() => {
                taskIds.forEach(id => {
                    const checkbox = document.querySelector(
                        `.task-select[value="${id}"]`
                    );
                    const row = checkbox?.closest('tr');
                    if (row) row.remove();
                });

                bulkActions.classList.add('hidden');
                if (selectAllCheckbox) selectAllCheckbox.checked = false;
            })
            .catch(() => {
                alert('Failed to delete selected tasks');
            });
        });
    }

});
