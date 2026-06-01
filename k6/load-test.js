import http from 'k6/http';
import { check, sleep } from 'k6';
import exec from 'k6/execution';

export const options = {
    scenarios: {
        coupon_issue: {
            executor: 'shared-iterations',
            vus: 1000,
            iterations: 1500,
            maxDuration: '2m',
        },
    },
};

export default function () {
    const userId = exec.scenario.iterationInTest + 1;

    const res = http.post(
        'http://localhost:8080/api/coupons/3/issue',
        JSON.stringify({ userId }),
        {
            headers: {
                'Content-Type': 'application/json',
            },
        }
    );

    check(res, {
        'status is expected': (r) => r.status === 200 || r.status === 400,
    });

    sleep(1);
}