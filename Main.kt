import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

class Philosopher(private val index: Int, private val leftFork: ReentrantLock, private val rightFork: ReentrantLock) {
    private val maxAttempts = 5
    private var attempts = 0

    fun think() {
        println("Философ $index размышляет.")
        Thread.sleep((1..5).random() * 1000L)
    }

    fun eat() {
        println("Философ $index обедает.")
        Thread.sleep((1..5).random() * 1000L)
        println("Философ $index закончил обед.")
    }

    fun tryTakeForks() {
        while (attempts < maxAttempts) {
            val fork1Acquired = leftFork.tryLock()
            if (fork1Acquired) {
                try {
                    val fork2Acquired = rightFork.tryLock()
                    if (fork2Acquired) {
                        try {
                            eat()
                            return
                        } finally {
                            rightFork.unlock()
                        }
                    }
                } finally {
                    leftFork.unlock()
                }
            }
            Thread.sleep(100)
            attempts++
        }
    }

    fun dine() {
        think()
        tryTakeForks()
    }
}

fun main() {
    val numPhilosophers = 5
    val forks = List(numPhilosophers) { ReentrantLock() }

    val philosophers = List(numPhilosophers) { index ->
        Philosopher(index, forks[index], forks[(index + 1) % numPhilosophers])
    }

    val threads = mutableListOf<Thread>()

    for (philosopher in philosophers) {
        threads += thread {
            philosopher.dine()
        }
    }

    threads.forEach { it.join() }

    println("Все философы закончили обед.")
}
