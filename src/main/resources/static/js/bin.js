document.addEventListener('DOMContentLoaded', () => {


    const selectAll = document.getElementById('selectAllBin');
    const bulkActions = document.getElementById('binBulkActions');


    function getCsrfHeaders() {
        const token = document.querySelector('meta[name="_csrf"]');
        const header = document.querySelector('meta[name="_csrf_header"]');
        if (!token || !header) return {};
        return { [header.content]: token.content };
    }


    function selectedCheckboxes() {
        return Array.from(document.querySelectorAll('.bin-select:checked'));
    }

    function selectedIds() {
        return selectedCheckboxes().map(cb => cb.value);
    }

    function removeRowsByIds(ids) {
        ids.forEach(id => {
            const checkbox = document.querySelector(
                `.bin-select[value="${id}"]`
            );
            const row = checkbox?.closest('tr');
            if (row) row.remove();
        });

        toggleBulkActions();
    }

    function toggleBulkActions() {
        bulkActions.classList.toggle('hidden', selectedIds().length === 0);
    }


    selectAll?.addEventListener('change', () => {
        document.querySelectorAll('.bin-select').forEach(cb => {
            cb.checked = selectAll.checked;
        });
        toggleBulkActions();
    });

    document.addEventListener('change', e => {
        if (!e.target.classList.contains('bin-select')) return;
        toggleBulkActions();
    });


    document.getElementById('bulkRestoreBtn')
        ?.addEventListener('click', async () => {

        const ids = selectedIds();
        if (!ids.length) return;

        const res = await fetch('/api/tasks/bin/restore', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...getCsrfHeaders()
            },
            body: JSON.stringify(ids)
        });

        if (!res.ok) {
            alert('Failed to restore tasks');
            return;
        }

        removeRowsByIds(ids);
    });


    document.getElementById('bulkPermanentDeleteBtn')
        ?.addEventListener('click', async () => {

        const ids = selectedIds();
        if (!ids.length) return;

        if (!confirm('Delete permanently? This cannot be undone.')) return;

        const res = await fetch('/api/tasks/bin/delete', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...getCsrfHeaders()
            },
            body: JSON.stringify(ids)
        });

        if (!res.ok) {
            alert('Failed to delete tasks');
            return;
        }

        removeRowsByIds(ids);
    });


    document.addEventListener('click', async e => {

        const restoreBtn = e.target.closest('.restore-btn');
        const deleteBtn = e.target.closest('.permanent-delete-btn');

        if (restoreBtn) {
            const id = restoreBtn.dataset.id;

            const res = await fetch(`/api/tasks/bin/restore/${id}`, {
                method: 'POST',
                headers: getCsrfHeaders()
            });

            if (!res.ok) {
                alert('Failed to restore task');
                return;
            }

            removeRowsByIds([id]);
        }

        if (deleteBtn) {
            if (!confirm('Delete permanently?')) return;

            const id = deleteBtn.dataset.id;

            const res = await fetch(`/api/tasks/bin/delete/${id}`, {
                method: 'DELETE',
                headers: getCsrfHeaders()
            });

            if (!res.ok) {
                alert('Failed to delete task');
                return;
            }

            removeRowsByIds([id]);
        }
    });

});
