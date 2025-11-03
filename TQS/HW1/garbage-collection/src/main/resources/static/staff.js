document.addEventListener('DOMContentLoaded', () => {

    const municipalitySelect = document.getElementById('municipalitySelect');
    const bookingsTbody = document.getElementById('bookingsTbody');

    // 1. Carrega os municípios para a dropdown, tal como na outra página.
    fetch('/api/bookings/municipalities')
        .then(response => response.json())
        .then(municipalities => {
            municipalitySelect.innerHTML = '<option value="">Selecione um município para ver os agendamentos</option>';
            municipalities.forEach(mun => {
                const option = document.createElement('option');
                option.value = mun;
                option.textContent = mun;
                municipalitySelect.appendChild(option);
            });
        })
        .catch(error => console.error('Erro ao carregar municípios:', error));

    // 2. Adiciona um "escutador" que reage quando o staff muda o município na dropdown.
    municipalitySelect.addEventListener('change', () => {
        const selectedMunicipality = municipalitySelect.value;
        bookingsTbody.innerHTML = ''; // Limpa a tabela antes de carregar novos dados.

        if (!selectedMunicipality) {
            return; // Se o utilizador selecionar "Selecione...", não faz nada.
        }

        // 3. Vai buscar os agendamentos para o município selecionado.
        fetch(`/api/bookings/staff/${selectedMunicipality}`)
            .then(response => response.json())
            .then(bookings => {
                if (bookings.length === 0) {
                    bookingsTbody.innerHTML = '<tr><td colspan="5">Não existem agendamentos para este município.</td></tr>';
                    return;
                }

                // Cria uma linha na tabela para cada agendamento.
                bookings.forEach(booking => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${booking.id}</td>
                        <td>${booking.itemDescription}</td>
                        <td>${booking.fullAddress}</td>
                        <td>${booking.bookingDate}</td>
                        <td>${booking.status}</td>
                    `;
                    bookingsTbody.appendChild(row);
                });
            })
            .catch(error => {
                console.error('Erro ao carregar agendamentos:', error);
                bookingsTbody.innerHTML = '<tr><td colspan="5" style="color: red;">Erro ao carregar dados.</td></tr>';
            });
    });
});