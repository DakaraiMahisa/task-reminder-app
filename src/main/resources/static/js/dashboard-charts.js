
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
});