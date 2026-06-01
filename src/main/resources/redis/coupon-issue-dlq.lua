if redis.call('SISMEMBER', KEYS[2], ARGV[1]) == 1 then
    return 0
end

redis.call(
        'XADD',
        KEYS[1],
        '*',
        'originalRecordId', ARGV[1],
        'couponId', ARGV[2],
        'userId', ARGV[3],
        'issuedAt', ARGV[4],
        'errorMessage', ARGV[5],
        'failedAt', ARGV[6],
        'deliveryCount', ARGV[7]
)

redis.call('SADD', KEYS[2], ARGV[1])

return 1