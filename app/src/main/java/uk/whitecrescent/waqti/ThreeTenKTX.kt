@file:Suppress("NOTHING_TO_INLINE", "UNUSED")

package uk.whitecrescent.waqti

import kotlin.random.Random

//region TypeAliases

//region Root

typealias Time = org.threeten.bp.LocalDateTime
typealias Duration = org.threeten.bp.Duration
typealias Date = org.threeten.bp.LocalDate
typealias ZonedTime = org.threeten.bp.ZonedDateTime
typealias LocalTime = org.threeten.bp.LocalTime
typealias Instant = org.threeten.bp.Instant
typealias DayOfWeek = org.threeten.bp.DayOfWeek
typealias Year = org.threeten.bp.Year
typealias YearMonth = org.threeten.bp.YearMonth
typealias Month = org.threeten.bp.Month
typealias MonthDay = org.threeten.bp.MonthDay
typealias ZoneOffset = org.threeten.bp.ZoneOffset

//endregion Root

//region Temporal

typealias Temporal = org.threeten.bp.temporal.Temporal
typealias TemporalAmount = org.threeten.bp.temporal.TemporalAmount
typealias TemporalUnit = org.threeten.bp.temporal.TemporalUnit
typealias ChronoUnit = org.threeten.bp.temporal.ChronoUnit
typealias ChronoField = org.threeten.bp.temporal.ChronoField

//endregion Temporal

//region Chrono

typealias ChronoLocalDate = org.threeten.bp.chrono.ChronoLocalDate
typealias ChronoLocalDateTime<D> = org.threeten.bp.chrono.ChronoLocalDateTime<D>

//endregion Chrono

//region Format

typealias DateTimeFormatter = org.threeten.bp.format.DateTimeFormatter

//endregion Format

//endregion TypeAliases

//region Extensions

//region Values

inline val now: Time
    get() = Time.now()

inline val today: Date
    get() = Date.now()

inline val tomorrow: Date
    get() = Date.now().plusDays(1)

inline val yesterday: Date
    get() = Date.now().minusDays(1)

inline val Number.millis: Duration
    get() = Duration.ofMillis(this.toLong())

inline val Number.seconds: Duration
    get() = Duration.ofSeconds(this.toLong())

inline val Number.minutes: Duration
    get() = Duration.ofMinutes(this.toLong())

inline val Number.hours: Duration
    get() = Duration.ofHours(this.toLong())

inline val Number.days: Duration
    get() = Duration.ofDays(this.toLong())

inline val Number.weeks: Duration
    get() = Duration.ofDays(7L * this.toLong())

inline val Number.am: LocalTime
    get() = LocalTime.of(this.toInt(), 0)

inline val Number.pm: LocalTime
    get() = LocalTime.of(this.toInt() + 12, 0)

inline val Pair<Number, Number>.am: LocalTime
    get() = LocalTime.of(this.first.toInt(), this.second.toInt())

inline val Pair<Number, Number>.pm: LocalTime
    get() = LocalTime.of(this.first.toInt() + 12, this.second.toInt())

inline val Triple<Number, Number, Number>.am: LocalTime
    get() = LocalTime.of(this.first.toInt(), this.second.toInt(), this.third.toInt())

inline val Triple<Number, Number, Number>.pm: LocalTime
    get() = LocalTime.of(this.first.toInt() + 12, this.second.toInt(), this.third.toInt())

inline val Duration.millis: Long
    get() = this.toMillis()

inline val Duration.secs: Double
    get() = (this.millis) / 1000.0

inline val <D : ChronoLocalDate> ChronoLocalDateTime<D>.isInThePast: Boolean
    get() = now isAfter this

inline val <D : ChronoLocalDate> ChronoLocalDateTime<D>.isInTheFuture: Boolean
    get() = now isBefore this

inline val randomDuration: Duration
    get() = Duration.of(Random.nextLong(), ChronoUnit.MILLIS)

inline val randomTime: Time
    get() = time(
            year = Random.nextInt(Year.MIN_VALUE, Year.MAX_VALUE),
            month = Random.nextInt(1, 13),
            dayOfMonth = Random.nextInt(1, 32),
            hour = Random.nextInt(0, 24),
            minute = Random.nextInt(0, 60),
            second = Random.nextInt(0, 60),
            nanoOfSecond = Random.nextInt(0, 1_000_000_000))

//endregion Values

//region Functions

inline fun time(year: Int, month: Int, dayOfMonth: Int,
                hour: Int = 0, minute: Int = 0, second: Int = 0, nanoOfSecond: Int = 0) =
        Time.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond)!!

inline fun time(year: Int, month: Month, dayOfMonth: Int,
                hour: Int = 0, minute: Int = 0, second: Int = 0, nanoOfSecond: Int = 0) =
        Time.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond)!!

inline fun time(hour: Number, minute: Number) = Pair(hour, minute)

inline fun time(hour: Number, minute: Number, second: Number) = Triple(hour, minute, second)

inline infix fun Date.at(time: LocalTime): Time = this.atTime(time)

inline infix fun Date.at(hour: Int): Time = this.atTime(hour, 0)

inline infix fun Date.at(pair: Pair<Number, Number>): Time =
        this.atTime(pair.first.toInt(), pair.second.toInt())

inline infix fun Date.at(triple: Triple<Number, Number, Number>): Time =
        this.atTime(triple.first.toInt(), triple.second.toInt(), triple.third.toInt())

inline infix fun Number.colon(other: Number): Pair<Number, Number> = this to other

inline infix fun Temporal.till(other: Temporal): Duration = Duration.between(this, other)

inline infix fun Duration.from(temporalAmount: TemporalAmount): Duration = Duration.from(temporalAmount)

inline infix fun <D : ChronoLocalDate>
        ChronoLocalDateTime<D>.isAfter(other: ChronoLocalDateTime<*>) = this.isAfter(other)

inline infix fun <D : ChronoLocalDate>
        ChronoLocalDateTime<D>.isBefore(other: ChronoLocalDateTime<*>) = this.isBefore(other)

inline infix fun <D : ChronoLocalDate>
        ChronoLocalDateTime<D>.isEqual(other: ChronoLocalDateTime<*>) = this.isEqual(other)

inline fun coming(dayOfWeek: DayOfWeek) =
        Date.from(today.dayOfWeek + dayOfWeek.value.toLong())!!

inline fun last(dayOfWeek: DayOfWeek) =
        Date.from(today.dayOfWeek - dayOfWeek.value.toLong())!!

inline fun randomTimeInFuture(from: Time = now): Time = from + randomDuration

inline fun randomTimeInPast(from: Time = now): Time = from - randomDuration

inline fun randomTimeBetween(from: Time, to: Time): Time {
    if (from.isEqual(to)) return from
    val before = if (from.isBefore(to)) from else to
    val after = if (from.isAfter(to)) from else to

    var result = after

    while (result.isAfter(after) && result.isBefore(before)) {
        if (result.isBefore(before)) result = randomTimeInFuture(before)
        if (result.isAfter(after)) result = randomTimeInPast(after)
    }
    return result
}

//endregion Functions

//region Other

inline val Time.toEpoch: Long
    get() = this.toEpochSecond(ZoneOffset.UTC)

inline val Time.rfcFormatted: String
    get() = this.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME)

//endregion Other

//endregion Extensions