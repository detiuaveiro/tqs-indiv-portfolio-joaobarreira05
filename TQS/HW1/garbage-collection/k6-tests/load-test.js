import http from 'k6/http';
import { check, sleep } from 'k6';

// Escala rápida para medir criação+cancelamento de marcações.
export const options = {
  stages: [
    { duration: '20s', target: 10 },
    { duration: '20s', target: 10 },
    { duration: '20s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<800'],
    checks: ['rate>0.95'],
  },
};

const API_HOST = __ENV.API_HOST || 'http://localhost:8080';
const BOOKINGS_ENDPOINT = `${API_HOST}/api/bookings`;
const MUNICIPALITIES = ['Lisboa', 'Porto', 'Coimbra', 'Faro', 'Braga', 'Aveiro', 'Sintra'];

function isoDateDaysFromNow(daysAhead) {
  const date = new Date();
  date.setDate(date.getDate() + daysAhead);
  return date.toISOString().substring(0, 10);
}

export default function () {
  const uniqueIndex = __ITER + __VU * 1000;
  const municipality = MUNICIPALITIES[uniqueIndex % MUNICIPALITIES.length];
  const bookingDate = isoDateDaysFromNow(3 + (uniqueIndex % 365));
  const timeSlot = uniqueIndex % 2 === 0 ? 'MORNING' : 'AFTERNOON';
  const payload = JSON.stringify({
    itemDescription: `Carga VU=${__VU} ITER=${__ITER}`,
    municipality,
    fullAddress: 'Rua do Teste de Carga, 123',
    bookingDate,
    timeSlot,
  });

  const params = { headers: { 'Content-Type': 'application/json' } };

  const createRes = http.post(BOOKINGS_ENDPOINT, payload, params);
  const creationOk = check(createRes, {
    'criação devolve 201': (r) => r.status === 201,
    'corpo contém token': (r) => r.json('bookingToken'),
  });

  if (creationOk) {
    const token = createRes.json('bookingToken');
    const cancelRes = http.del(`${BOOKINGS_ENDPOINT}/token/${token}`);
    check(cancelRes, {
      'cancelamento devolve 200': (r) => r.status === 200,
      'estado final é CANCELLED': (r) => r.json('status') === 'CANCELLED',
    });
  }

  sleep(1);
}