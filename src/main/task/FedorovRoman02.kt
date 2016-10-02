package taxiPark.task

import taxiPark.Driver
import taxiPark.Order
import taxiPark.Passenger
import taxiPark.TaxiPark

/*
Если вы не знаете, с какой стороны подступиться к заданию,
можно начать с части про коллекции в kotlin-koans
http://try.kotlinlang.org/#/Kotlin%20Koans/Collections/Introduction/Task.kt.
Там простые задачки на использование конкретных функций.
Их сдавать не надо, там есть ответы.
Домашнее задание по уровню сложности похоже на последние задания в koans,
когда нужно применить несколько функций.
*/

/*
Задание #0.
Строка (из прописных букв английского алфавита) называется красивой, если выполнены ВСЕ следующие условия:
1. она содержит по крайней мере три гласных буквы (гласные буквы: "aeiou")
2. она содержит по крайней мере одну сдвоенную букву (например, ss в строке "klsst")
3. она не содержит сочетания (подстроки) "bu", "ba" и "be" (даже если они являются частью предыдущих условий)
Напишите функцию, которая проверяет, что строка красивая.
Желательно, чтобы функция была небольшого размера и понятная. Четырех строк достаточно :)
В TestNiceStrings есть примеры с комментариями.
 */
fun String.isNice(): Boolean {
    return count { it in  "aeiou" } >= 3 &&
            filterIndexed { pos, letter -> getOrNull(pos + 1) == letter }.length >= 1 &&
            !contains("bu") && !contains("ba") && !contains("be")
}

/*
Для всех остальных заданий используются классы TaxiPark, Driver, Passenger и Order, объявленные в файле TaxiPark.kt.
Цель: понятный код. Можно объявлять столько дополнительных функций, сколько потребуется.
*/

// Задание #1.
// Найти водителей, которые не выполнили ни одного заказа
fun TaxiPark.findFakeDrivers(): Collection<Driver> =
        allDrivers.filterNot { it in orders.map(Order::driver) }

// Задание #2.
// Найти всех клиентов, у которых больше заданного числа поездок
fun TaxiPark.findFaithfulPassengers(minTrips: Int): List<Passenger> =
        allPassengers.filter { passenger -> orders.count { it.hasPassenger(passenger) } > minTrips }


// Задание #3.
// Найти всех пассажиров, которых данный водитель возил больше одного раза
fun TaxiPark.findFrequentPassengers(driver: Driver): List<Passenger> =
        allPassengers.filter { ordersWithDriverAndPassenger(driver, it).size > 1 }

// Задание #4.
// Найти пассажиров, которые большую часть поездок осуществили со скидками
fun TaxiPark.findSmartPassengers() =
        allPassengers.filter { firstPartBigger(discountOrdersFirstUsualSecond(it)) }

// Задание #5.
// Найти самый частый интервал поездок среди 0-9 минут, 10-19 минут, 20-29 минут и т.д.
// Если нет заказов - вернуть null.
fun TaxiPark.findTheMostFrequentTripDuration(): IntRange? {
    val count = IntArray(1 + getMaxDuration() / 10)
    orders.forEach { count[it.duration / 10]++ }
    val m = maxMultipliedBy10(count)
    return if (orders.size == 0) null else (m?.rangeTo(m + 9) ?: null)
}

// Задание #6.
// Узнать: правда ли, что 20% водителей приносят 80% прибыли?
fun TaxiPark.checkParetoPrinciple(): Boolean =
        sortDriversByEarnings().take((allDrivers.size * 0.2).toInt()).sumByDouble { it.second } >=
                0.8 * earnedByDrivers(allDrivers)

fun TaxiPark.sortDriversByEarnings(): List<Pair<Driver, Double>> =
        orders
                .groupBy { it.driver }
                .map { Pair(it.key, it.value.sumByDouble { order -> order.cost }) }
                .sortedByDescending { p -> p.second }

fun TaxiPark.earnedByDrivers(drivers: List<Driver>): Double =
        orders.filter { it.driver in drivers }.sumByDouble { it.cost }

fun Order.hasPassenger(passenger: Passenger): Boolean =
        passenger in passengers

fun TaxiPark.getMaxDuration(): Int =
        orders.maxBy { it.duration }?.duration ?: 0

fun TaxiPark.ordersWithDriverAndPassenger(driver: Driver, passenger: Passenger): Collection<Order> =
        orders.filter { it.driver.equals(driver) && it.hasPassenger(passenger) }

fun TaxiPark.ordersForPassenger(passenger: Passenger): Collection<Order> =
        orders.filter { it.hasPassenger(passenger) }

fun TaxiPark.discountOrdersFirstUsualSecond(passenger: Passenger): Pair<List<Order>, List<Order>> =
        ordersForPassenger(passenger).partition { it.discount != null }

val firstPartBigger = { x: Pair<List<Any>, List<Any>> -> x.component1().size > x.component2().size }

val getMaxPos = { i: IntArray -> i.indexOf(i.max()?.toInt() ?: -1) }
val maxMultipliedBy10: (IntArray) -> Int? = { i -> if (getMaxPos(i) != -1) 10 * getMaxPos(i) else null }
