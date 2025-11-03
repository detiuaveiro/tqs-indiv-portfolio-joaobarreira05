// Executa o nosso código quando o HTML da página estiver completamente carregado.
document.addEventListener('DOMContentLoaded', () => {

    // --- Elementos do DOM que vamos manipular ---
    const municipalitySelect = document.getElementById('municipality');
    const bookingForm = document.getElementById('bookingForm');
    const resultDiv = document.getElementById('result');
    const statusForm = document.getElementById('statusForm');
    const statusResultDiv = document.getElementById('statusResult');

    // --- Lógica Principal ---

    // 1. Carregar os municípios assim que a página abre.
    fetch('/api/bookings/municipalities')
        .then(response => response.json()) // Converte a resposta para JSON.
        .then(municipalities => {
            municipalitySelect.innerHTML = '<option value="">Selecione um município</option>'; // Limpa a opção "A carregar...".
            municipalities.forEach(mun => {
                const option = document.createElement('option');
                option.value = mun;
                option.textContent = mun;
                municipalitySelect.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Erro ao carregar municípios:', error);
            municipalitySelect.innerHTML = '<option value="">Não foi possível carregar</option>';
        });

    // 2. Lidar com a submissão do formulário de agendamento.
    bookingForm.addEventListener('submit', event => {
        event.preventDefault(); // Impede que a página recarregue ao submeter.

        // Recolhe os dados do formulário.
        const formData = new FormData(bookingForm);
        const data = Object.fromEntries(formData.entries());

        // Faz o pedido POST para a nossa API.
        fetch('/api/bookings', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data), // Converte o nosso objeto de dados para uma string JSON.
        })
        .then(async response => {
            if (!response.ok) {
                throw new Error(await extractErrorMessage(response) || 'Falha no agendamento. Verifique os dados.');
            }
            return response.json();
        })
        .then(booking => {
            renderBookingCreationSuccess(resultDiv, booking);
            bookingForm.reset(); // Limpa o formulário.
        })
        .catch(error => {
            renderError(resultDiv, error.message);
        });
    });

    // 3. Lidar com a consulta de estado do agendamento.
    statusForm.addEventListener('submit', event => {
        event.preventDefault();
        const token = document.getElementById('bookingToken').value;

        if (!token) return;

        fetch(`/api/bookings/token/${token}`)
            .then(async response => {
                if (response.status === 404) {
                    throw new Error('Código de agendamento não encontrado.');
                }
                if (!response.ok) {
                    throw new Error(await extractErrorMessage(response) || 'Ocorreu um erro ao consultar.');
                }
                return response.json();
            })
            .then(booking => {
                renderBookingDetails(statusResultDiv, booking, true);
            })
            .catch(error => {
                renderError(statusResultDiv, error.message);
            });
    });

    async function extractErrorMessage(response) {
        try {
            const data = await response.json();
            if (data && typeof data === 'object' && data.message) {
                return data.message;
            }
        } catch (err) {
            try {
                const text = await response.text();
                if (text) {
                    return text;
                }
            } catch (_) {
                return null;
            }
        }
        return null;
    }

    function renderError(container, message) {
        container.style.display = 'block';
        container.innerHTML = `<p style="color: red;">${message}</p>`;
    }

    function formatHistory(history = []) {
        if (!Array.isArray(history) || history.length === 0) {
            return '<p>Sem histórico disponível.</p>';
        }

        const items = history
            .map(entry => {
                const timestamp = new Date(entry.changedAt).toLocaleString('pt-PT');
                const note = entry.note ? ` — ${entry.note}` : '';
                return `<li><strong>${entry.status}</strong> em ${timestamp}${note}</li>`;
            })
            .join('');

        return `<ul>${items}</ul>`;
    }

    function renderBookingCreationSuccess(container, booking) {
        container.style.display = 'block';
        container.innerHTML = `
            <h3>Agendamento realizado com sucesso!</h3>
            <p>Guarde o seu código de consulta: <strong>${booking.bookingToken}</strong></p>
            <h4>Detalhes</h4>
            <p><strong>Município:</strong> ${booking.municipality}</p>
            <p><strong>Data:</strong> ${booking.bookingDate}</p>
            <p><strong>Estado atual:</strong> ${booking.status}</p>
            <h4>Linha temporal</h4>
            ${formatHistory(booking.history)}
        `;
    }

    function renderBookingDetails(container, booking, allowCancel = false) {
        container.style.display = 'block';

        const canCancel = allowCancel && booking.status !== 'CANCELLED' && booking.status !== 'COMPLETED';

        container.innerHTML = `
            <h3>Detalhes do Agendamento</h3>
            <p><strong>Código:</strong> ${booking.bookingToken}</p>
            <p><strong>Município:</strong> ${booking.municipality}</p>
            <p><strong>Descrição:</strong> ${booking.itemDescription || '—'}</p>
            <p><strong>Morada:</strong> ${booking.fullAddress || '—'}</p>
            <p><strong>Data:</strong> ${booking.bookingDate}</p>
            <p><strong>Período:</strong> ${booking.timeSlot}</p>
            <p><strong>Estado:</strong> ${booking.status}</p>
            <h4>Linha temporal</h4>
            ${formatHistory(booking.history)}
            ${canCancel ? '<button type="button" id="cancelBookingBtn">Cancelar agendamento</button>' : ''}
        `;

        if (canCancel) {
            const cancelButton = container.querySelector('#cancelBookingBtn');
            cancelButton.addEventListener('click', async () => {
                cancelButton.disabled = true;
                cancelButton.textContent = 'A cancelar...';
                try {
                    const updated = await cancelBooking(booking.bookingToken);
                    renderBookingDetails(container, updated, false);
                } catch (error) {
                    renderError(container, error.message || 'Não foi possível cancelar.');
                }
            });
        }
    }

    async function cancelBooking(token) {
        const response = await fetch(`/api/bookings/token/${token}`, { method: 'DELETE' });
        if (!response.ok) {
            throw new Error(await extractErrorMessage(response) || 'Não foi possível cancelar o agendamento.');
        }
        return response.json();
    }
});