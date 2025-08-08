import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Counter } from 'k6/metrics';

export let options = {
  vus: __ENV.VUS ? parseInt(__ENV.VUS) : 1000,
  duration: __ENV.DURATION || '60s',
  thresholds: {
    http_req_failed: ['rate<0.05'],
    http_req_duration: ['p(95)<1000'],
  },
};

const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';
const studyIds = Array.from({length: 20}, (_, i) => i + 1);
const maxUserId = 2000;

const updateTrend = new Trend('location_update_duration');
const allTrend = new Trend('location_all_duration');
const updates = new Counter('location_updates');
const allFetches = new Counter('location_all_fetches');

export default function () {
  const userId = Math.floor(Math.random() * maxUserId) + 1;
  const studyId = studyIds[Math.floor(Math.random() * studyIds.length)];
  
  const lat = 37.5665 + (Math.random() - 0.5) * 0.01;
  const lon = 126.9780 + (Math.random() - 0.5) * 0.01;

  const updateRes = http.post(
    `${baseUrl}/api/study/${studyId}/location/update`,
    JSON.stringify({ latitude: lat, longitude: lon }),
    {
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': userId.toString(),
      },
    },
  );
  check(updateRes, {
    'update status 200': (r) => r.status === 200,
  });
  updateTrend.add(updateRes.timings.duration);
  updates.add(1);

  const allRes = http.get(`${baseUrl}/api/study/${studyId}/location/all`, {
    headers: { 'X-User-Id': userId.toString() },
  });
  check(allRes, {
    'all status 200': (r) => r.status === 200,
  });
  allTrend.add(allRes.timings.duration);
  allFetches.add(1);

  sleep(Math.random() * 0.4 + 0.1);
}
