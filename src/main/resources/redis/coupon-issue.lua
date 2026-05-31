local issuedCountKey = KEYS[1]
local userIssuedKey = KEYS[2]
local issuedStreamKey = KEYS[3]

local totalQuantity = tonumber(ARGV[1])
local ttlSeconds = tonumber(ARGV[2])
local couponId = ARGV[3]
local userId = ARGV[4]
local issuedAt = ARGV[5]

-- 이미 발급받은 사용자
if redis.call('EXISTS', userIssuedKey) == 1 then
    return 1
end

local currentIssueCount = tonumber(redis.call('GET', issuedCountKey) or '0')

-- 수량 초과
if currentIssueCount >= totalQuantity then
    return 2
end

-- 발급 처리
redis.call('INCR', issuedCountKey)

if ttlSeconds > 0 then
    redis.call('SET', userIssuedKey, '1', 'EX', ttlSeconds)
    redis.call('EXPIRE', issuedCountKey, ttlSeconds)
else
    redis.call('SET', userIssuedKey, '1')
end

-- 발급 성공 이벤트 저장
redis.call(
        'XADD',
        issuedStreamKey,
        '*',
        'couponId', couponId,
        'userId', userId,
        'issuedAt', issuedAt
)

-- 성공
return 0