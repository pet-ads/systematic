package br.all.domain.shared.utils

import io.github.serpro69.kfaker.Faker

fun Faker.paragraph(size: Int) =
    List(this.random.nextInt(1, size)) { this.lorem.words() }.joinToString(" ")

fun Faker.paragraphList(paragraphMaxSize: Int, listSize: Int) =
    List(this.random.nextInt(0, listSize)) { paragraph(paragraphMaxSize) }

fun Faker.year() = random.nextInt(1900, 2050)

fun Faker.jsonWordsArray(size: Int) =
    List(this.random.nextInt(0, size)) { "\"${this.lorem.words()}\"" }.joinToString(",", "[", "]")

fun Faker.wordsList(maxSize:Int, minSize: Int = 0) = List(this.random.nextInt(minSize, maxSize)) { this.lorem.words() }
