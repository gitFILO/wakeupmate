import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Counter } from 'k6/metrics';

export let options = {
  vus: __ENV.VUS ? parseInt(__ENV.VUS) : 100,
  duration: __ENV.DURATION || '30s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<400'],
  },
};

const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';
const studyId = __ENV.STUDY_ID ? parseInt(__ENV.STUDY_ID) : 1;
const userIdHeader = __ENV.USER_ID_HEADER || '1';

const updateTrend = new Trend('location_update_duration');
const allTrend = new Trend('location_all_duration');
const updates = new Counter('location_updates');
const allFetches = new Counter('location_all_fetches');

export default function () {
  const lat = 37.5665 + Math.random() * 0.0001;
  const lon = 126.9780 + Math.random() * 0.0001;

  // 1) update location
  const updateRes = http.post(
    `${baseUrl}/api/study/${studyId}/location/update`,
    JSON.stringify({ latitude: lat, longitude: lon }),
    {
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': userIdHeader,
      },
    },
  );
  check(updateRes, {
    'update status 200': (r) => r.status === 200,
  });
  updateTrend.add(updateRes.timings.duration);
  updates.add(1);

  // 2) fetch all locations
  const allRes = http.get(`${baseUrl}/api/study/${studyId}/location/all`, {
    headers: { 'X-User-Id': userIdHeader },
  });
  check(allRes, {
    'all status 200': (r) => r.status === 200,
  });
  allTrend.add(allRes.timings.duration);
  allFetches.add(1);

  sleep(1);
}
