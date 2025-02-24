package br.ifsp.octaviano

import br.all.domain.services.SelectionStatusSuggestionService
import br.all.domain.services.SelectionStatusSuggestionService.RequestModel
import br.all.domain.services.SelectionStatusSuggestionService.StudyReviewInfo

fun main() {
    val input = listOf(
        StudyReviewInfo(1, 84, 1, 2006),
        StudyReviewInfo(2, 83, 0, 2006),
        StudyReviewInfo(3, 71, 1, 2006),
        StudyReviewInfo(4, 59, 0, 2007),
        StudyReviewInfo(5, 56, 0, 2007),
        StudyReviewInfo(6, 55, 6, 2004),
        StudyReviewInfo(7, 55, 10, 2006),
        StudyReviewInfo(8, 55, 9, 2006),
        StudyReviewInfo(9, 52, 18, 2003),
        StudyReviewInfo(10, 49, 35, 2004),
        StudyReviewInfo(11, 48, 2, 2006),
        StudyReviewInfo(12, 45, 0, 2005),
        StudyReviewInfo(13, 44, 2, 2006),
        StudyReviewInfo(14, 43, 3, 2005),
        StudyReviewInfo(15, 41, 0, 2006),
        StudyReviewInfo(16, 40, 0, 2007),
        StudyReviewInfo(17, 39, 0, 2007),
        StudyReviewInfo(18, 38, 0, 2006),
        StudyReviewInfo(19, 38, 7, 2005),
        StudyReviewInfo(20, 38, 7, 2005),
        StudyReviewInfo(21, 35, 1, 2007),
        StudyReviewInfo(22, 35, 0, 2007),
        StudyReviewInfo(23, 35, 0, 2007),
        StudyReviewInfo(24, 34, 0, 2007),
        StudyReviewInfo(25, 34, 1, 2006),
        StudyReviewInfo(26, 34, 0, 2006),
        StudyReviewInfo(27, 32, 0, 2007),
        StudyReviewInfo(28, 32, 0, 2007),
        StudyReviewInfo(29, 32, 2, 2005),
        StudyReviewInfo(30, 32, 0, 2007),
        StudyReviewInfo(31, 31, 1, 2004),
        StudyReviewInfo(32, 31, 0, 2007),
        StudyReviewInfo(33, 31, 8, 2002),
        StudyReviewInfo(34, 29, 0, 2006),
        StudyReviewInfo(35, 29, 0, 2006),
        StudyReviewInfo(36, 29, 0, 2007),
        StudyReviewInfo(37, 29, 9, 2006),
        StudyReviewInfo(38, 28, 0, 2006),
        StudyReviewInfo(39, 28, 0, 2007),
        StudyReviewInfo(40, 27, 0, 2006),
        StudyReviewInfo(41, 27, 0, 2006),
        StudyReviewInfo(42, 26, 0, 2005),
        StudyReviewInfo(43, 26, 4, 2005),
        StudyReviewInfo(44, 25, 0, 2006),
        StudyReviewInfo(45, 25, 1, 2006),
        StudyReviewInfo(46, 25, 6, 2005),
        StudyReviewInfo(47, 24, 0, 2007),
        StudyReviewInfo(48, 23, 0, 2006),
        StudyReviewInfo(49, 22, 0, 2007),
        StudyReviewInfo(50, 22, 0, 2004),
        StudyReviewInfo(51, 22, 3, 2006),
        StudyReviewInfo(52, 22, 0, 2005),
        StudyReviewInfo(53, 22, 0, 2007),
        StudyReviewInfo(54, 20, 6, 2006),
        StudyReviewInfo(55, 20, 1, 2004),
        StudyReviewInfo(56, 20, 0, 2007),
        StudyReviewInfo(57, 18, 0, 2007),
        StudyReviewInfo(58, 18, 3, 2005),
        StudyReviewInfo(59, 17, 0, 2003),
        StudyReviewInfo(60, 17, 6, 2005),
        StudyReviewInfo(61, 17, 1, 2005),
        StudyReviewInfo(62, 17, 0, 2007),
        StudyReviewInfo(63, 16, 0, 2006),
        StudyReviewInfo(64, 16, 0, 2006),
        StudyReviewInfo(65, 16, 0, 2007),
        StudyReviewInfo(66, 15, 0, 2004),
        StudyReviewInfo(67, 15, 0, 2000),
        StudyReviewInfo(68, 15, 0, 2007),
        StudyReviewInfo(69, 14, 0, 2007),
        StudyReviewInfo(70, 13, 0, 2007),
        StudyReviewInfo(71, 13, 5, 2005),
        StudyReviewInfo(72, 13, 0, 2006),
        StudyReviewInfo(73, 11, 3, 2005),
        StudyReviewInfo(74, 11, 0, 2007),
        StudyReviewInfo(75, 11, 0, 2007),
        StudyReviewInfo(76, 11, 0, 2007),
        StudyReviewInfo(77, 11, 0, 2007),
        StudyReviewInfo(78, 11, 0, 2007),
        StudyReviewInfo(79, 10, 0, 2006),
        StudyReviewInfo(80, 10, 0, 2007),
        StudyReviewInfo(81, 9, 0, 2000),
        StudyReviewInfo(82, 9, 0, 2007),
        StudyReviewInfo(83, 9, 0, 2006),
        StudyReviewInfo(84, 9, 0, 2007),
        StudyReviewInfo(85, 8, 0, 2007),
        StudyReviewInfo(86, 8, 0, 2006),
        StudyReviewInfo(87, 8, 0, 2006),
        StudyReviewInfo(88, 8, 0, 2002),
        StudyReviewInfo(89, 8, 0, 2006),
        StudyReviewInfo(90, 8, 0, 2007),
        StudyReviewInfo(91, 8, 0, 2003),
        StudyReviewInfo(92, 6, 0, 2006),
        StudyReviewInfo(93, 6, 0, 2007),
        StudyReviewInfo(94, 6, 0, 2003),
        StudyReviewInfo(95, 3, 0, 2006),
        StudyReviewInfo(96, 3, 0, 2007),
        StudyReviewInfo(97, 3, 0, 2007),
    )

    val service: SelectionStatusSuggestionService = ScasImpl()
    val responseModel = service.buildSuggestions(RequestModel(input))

    responseModel.studySuggestions.forEach { println(it) }
}
