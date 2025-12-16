document.addEventListener('DOMContentLoaded', function() {
    const numberInput = document.getElementById('numberInput');
    const calculateBtn = document.getElementById('calculateBtn');
    const clearBtn = document.getElementById('clearBtn');
    const resultText = document.getElementById('resultText');
    const loading = document.getElementById('loading');
    const statusText = document.getElementById('statusText');
    const exampleButtons = document.querySelectorAll('.example-btn');
    const dispatcherStatus = document.getElementById('dispatcherStatus');
    const processorItems = document.querySelectorAll('#processorList li span');

    // Direcciones de los servidores
    const dispatcherUrl = 'http://localhost:8080';
    const processorUrls = [
        'http://localhost:8081',
        'http://localhost:8082',
        'http://localhost:8083',
        'http://localhost:8084'
    ];

    // Verificar estado de los servidores
    function checkServersStatus() {
        // Verificar despachador
        fetch(`${dispatcherUrl}/status`)
            .then(response => {
                if (response.ok) {
                    dispatcherStatus.textContent = 'En línea';
                    dispatcherStatus.className = 'status-online';
                } else {
                    dispatcherStatus.textContent = 'Sin conexión';
                    dispatcherStatus.className = 'status-offline';
                }
            })
            .catch(() => {
                dispatcherStatus.textContent = 'Sin conexión';
                dispatcherStatus.className = 'status-offline';
            });

        // Verificar procesadores
        processorUrls.forEach((url, index) => {
            fetch(`${url}/status`)
                .then(response => {
                    if (response.ok) {
                        processorItems[index].textContent = 'En línea';
                        processorItems[index].className = 'status-online';
                    } else {
                        processorItems[index].textContent = 'Sin conexión';
                        processorItems[index].className = 'status-offline';
                    }
                })
                .catch(() => {
                    processorItems[index].textContent = 'Sin conexión';
                    processorItems[index].className = 'status-offline';
                });
        });
    }

    // Calcular factorial
    calculateBtn.addEventListener('click', calculateFactorial);
    
    numberInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            calculateFactorial();
        }
    });

    // Limpiar resultados
    clearBtn.addEventListener('click', function() {
        numberInput.value = '';
        resultText.textContent = 'El resultado aparecerá aquí...';
    });

    // Botones de ejemplo
    exampleButtons.forEach(button => {
        button.addEventListener('click', function() {
            const number = this.getAttribute('data-number');
            numberInput.value = number;
            calculateFactorial();
        });
    });

    async function calculateFactorial() {
        const number = numberInput.value.trim();
        
        if (!number || !/^\d+$/.test(number)) {
            alert('Por favor ingrese un número entero positivo válido');
            return;
        }

        // Mostrar loading
        loading.classList.remove('hidden');
        resultText.textContent = '';
        statusText.textContent = 'Enviando solicitud al despachador...';

        try {
            // Enviar solicitud al servidor despachador
            statusText.textContent = 'Calculando factorial distribuido...';
            
            const response = await fetch(`${dispatcherUrl}/factorial`, {
                method: 'POST',
                body: number,
                headers: {
                    'Content-Type': 'text/plain'
                }
            });

            if (!response.ok) {
                throw new Error(`Error del servidor: ${response.status}`);
            }

            const result = await response.text();
            
            // Formatear resultado
            const lines = result.split('\n');
            const formattedResult = lines.map(line => {
                if (line.includes('Factorial de')) {
                    return `<strong>${line}</strong>`;
                }
                return line;
            }).join('<br>');

            resultText.innerHTML = formattedResult;
            statusText.textContent = '¡Cálculo completado exitosamente!';

            // Actualizar estado de servidores
            checkServersStatus();

        } catch (error) {
            console.error('Error:', error);
            resultText.textContent = `Error: ${error.message}`;
            statusText.textContent = 'Error en el cálculo';
        } finally {
            // Ocultar loading después de 2 segundos
            setTimeout(() => {
                loading.classList.add('hidden');
            }, 2000);
        }
    }

    // Verificar estado inicial de servidores
    checkServersStatus();
    setInterval(checkServersStatus, 30000); // Verificar cada 30 segundos
});