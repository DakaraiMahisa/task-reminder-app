document.addEventListener('DOMContentLoaded', function () {

    const calendarEl = document.getElementById('taskCalendar');
    if (!calendarEl) {
        return;
    }

    const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

    const csrfToken = csrfTokenMeta ? csrfTokenMeta.content : null;
    const csrfHeader = csrfHeaderMeta ? csrfHeaderMeta.content : null;

    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        height: 650,

        editable: true,
        eventStartEditable: true,
        eventDurationEditable: false,

        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },

        events: '/api/tasks/calendar',

        eventClick: function (info) {
            window.location.href = '/api/view/' + info.event.id;
        },

        eventDrop: function (info) {
            const taskId = info.event.id;
            const newDueDate = info.event.start
            .toISOString()
            .replace('Z', '')
            .split('.')[0];
            fetch(`/api/tasks/${taskId}/due-date`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                    ...(csrfHeader && csrfToken ? { [csrfHeader]: csrfToken } : {})
                },
                body: JSON.stringify({
                    dueDate: newDueDate
                })
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Update failed');
                }
            })
            .catch(() => {
                alert('Could not update task date');
                info.revert();
            });
        },

        eventAllow: function (dropInfo, draggedEvent) {
            if (draggedEvent.extendedProps.status === 'DONE') {
                return false;
            }
        const newDate = new Date(dropInfo.start);

            const today = new Date();

            newDate.setHours(0, 0, 0, 0);
            today.setHours(0, 0, 0, 0);

            return newDate >= today;
        }
    });

    calendar.render();
});
