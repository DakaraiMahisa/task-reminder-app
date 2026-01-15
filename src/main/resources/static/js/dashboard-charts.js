
document.addEventListener('DOMContentLoaded', function () {
    const chartElement = document.getElementById('productivityChart');

    if (chartElement) {

        const labels = JSON.parse(chartElement.getAttribute('data-labels'));
        const values = JSON.parse(chartElement.getAttribute('data-values'));

        const ctx = chartElement.getContext('2d');

        const gradient = ctx.createLinearGradient(0, 0, 0, 400);
        gradient.addColorStop(0, 'rgba(0, 31, 63, 0.15)');
        gradient.addColorStop(1, 'rgba(0, 123, 255, 0)');

        new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Tasks Completed',
                    data: values,
                    fill: true,
                    backgroundColor: gradient,
                    borderColor: '#001f3f', // Navy
                    borderWidth: 3,
                    tension: 0.4,
                    pointBackgroundColor: '#007bff', // Blue
                    pointRadius: 5,
                    pointHoverRadius: 7
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    x: { grid: { display: false } },
                    y: {
                        beginAtZero: true,
                        ticks: { stepSize: 1 }
                    }
                }
            }
        });
    }
    const sidebar = document.getElementById('sidebar');
        const btn = document.getElementById('sidebarCollapse');
        const icon = btn.querySelector('i');

        // 1. Check local storage for preference
        if (localStorage.getItem('sidebar-collapsed') === 'true') {
            sidebar.classList.add('collapsed');
            icon.classList.replace('fa-angles-left', 'fa-angles-right');
        }

        // 2. Toggle Event
        btn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');

            // Update Icon
            if(sidebar.classList.contains('collapsed')) {
                icon.classList.replace('fa-angles-left', 'fa-angles-right');
                localStorage.setItem('sidebar-collapsed', 'true');
            } else {
                icon.classList.replace('fa-angles-right', 'fa-angles-left');
                localStorage.setItem('sidebar-collapsed', 'false');
            }
        });
});