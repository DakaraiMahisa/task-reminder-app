document.querySelector('.filter-form').addEventListener('submit', function (e) {

        const selectedFilters = document.querySelectorAll('input[name="filters"]:checked');

        if (selectedFilters.length === 0) {
            alert("Please select filter type");
            e.preventDefault();
            return;
        }

        for (let checkbox of selectedFilters) {
            const filter = checkbox.value;

            if (filter === 'status') {
                if (!document.querySelector('select[name="status"]').value) {
                    alert("Please fill the status field");
                    e.preventDefault();
                    return;
                }
            }

            if (filter === 'priority') {
                if (!document.querySelector('select[name="priority"]').value) {
                    alert("Please fill the priority field");
                    e.preventDefault();
                    return;
                }
            }

            if (filter === 'title') {
                if (!document.querySelector('input[name="title"]').value.trim()) {
                    alert("Please fill the title field");
                    e.preventDefault();
                    return;
                }
            }
        }
    });