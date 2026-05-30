local issuedCountKey = KEYS[1]
local userIssuedKey = KEYS[2]

local totalQuantity = tonumber(ARGV[1])
local ttlSeconds = tonumber(ARGV[2])

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
redis.call('SET', userIssuedKey, '1')

-- 이벤트 기간 이후 자동 정리용 TTL
if (ttlSeconds > 0) then
    redis.call('EXPIRE', issuedCountKey, ttlSeconds)
    redis.call('EXPIRE', userIssuedKey, ttlSeconds)
end

-- 성공
return 0