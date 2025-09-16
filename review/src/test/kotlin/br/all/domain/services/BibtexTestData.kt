package br.all.domain.services

object BibtexTestData {
    val testInputs = mapOf(
        "unknown type of bibtex" to """
            @{nash51,
                title = {Non-cooperative Games},
                year = {1951},
                author = {Nash, John},
                journal = {Annals of Mathematics},
                abstract = {Lorem Ipsum},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
                doi = {10.1234/doi}
            }
        """,

        "invalid title" to """
            @article{nash51,
                year = {1951},
                author = {Nash, John},
                journal = {Annals of Mathematics},
                abstract = {Lorem Ipsum},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
                doi = {10.1234/doi}
            }
        """,

        "invalid year" to """
            @article{nash51,
                title = {Non-cooperative Games},
                author = {Nash, John},
                journal = {Annals of Mathematics},
                abstract = {Lorem Ipsum},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
                doi = {10.1234/doi}
            }
        """,

        "invalid abstract" to """
            @article{nash51,
                title = {Non-cooperative Games},
                year = {1951},
                author = {Nash, John},
                journal = {Annals of Mathematics},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
                doi = {10.1234/doi}
            }
        """,

        "invalid venue" to """
            @article{nash51,
                title = {Non-cooperative Games},
                year = {1951},
                author = {Nash, John},
                abstract = {Lorem Ipsum},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
                doi = {10.1234/doi}
            }
        """,

        "invalid doi" to """
            @article{nash51,
                title = {Non-cooperative Games},
                year = {1951},
                author = {Nash, John},
                journal = {Annals of Mathematics},
                abstract = {Lorem Ipsum},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
            }
        """,

        "invalid authors" to """
            @article{nash51,
                title = {Non-cooperative Games},
                year = {1951},
                journal = {Annals of Mathematics},
                abstract = {Lorem Ipsum},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
                doi = {10.1234/doi}
            }
        """,

        "article missing optional fields" to """
            @article{CitekeyArticle,
                title = {The independence of the continuum hypothesis},
                year = {1963},
                author = {P. J. Cohen},
                journal = {Proceedings of the National Academy of Sciences},
                abstract = {Lorem Ipsum},
            }
        """,

        "valid article" to """
            @article{nash51,
                title = {Non-cooperative Games},
                year = {1951},
                author = {Nash, John},
                journal = {Annals of Mathematics},
                abstract = {Lorem Ipsum},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
                doi = {10.1234/doi}
            }
        """,

        "valid inproceedings" to """
            @INPROCEEDINGS{Rodrigues11MOSA,
              author    = {Onofre {Trindade Júnior}},
              title     = {{Using SOA in Critical-Embedded Systems}},
              booktitle = {Proceedings of the 4${'$'}^{th}${'$'}  IEEE (CPSCom'11)},
              year      = {2011},
              pages     = {733-738},
              address   = {Dalian, China},
              abstract  = {Lorem ipsum},
              references = {ref3, ref4},
              doi        = {10.1021/ci025584y}
            }
        """,

        "valid techreport" to """
            @Techreport{Durelli2011LPSRM,
                author    = {Rafael Serapilha Durelli},
                title     = {Uma abordagem apoiada por linguagens específicas de domínio para a criação de linhas de produto de software embarcado},
                institution  = {Universidade Federal de São Carlos (UFSCar)},
                year      = {2011},
                type      = {Dissertação de Mestrado},
                address   = {São Carlos, SP},
                abstract  = {Lorem Ipsum}
            }
        """,

        "valid book" to """
            @BOOK{Bass03SAPRr,
              title      = {Software Architecture in Practice},
              publisher  = {Addison-Wesley},
              year       = {2012},
              series     = {SEI Series in Software Engineering},
              edition    = {3},
              author     = {Len Bass and Paul Clements and Rick Kazman},
              abstract    = {Lorem Ipsum}
            }
        """,

        "valid proceedings" to """
            @proceedings{CitekeyProceedings,
              editor    = {Susan Stepney and Sergey Verlan},
              title     = {Proceedings of the 17th International Conference on Computation and Natural Computation, Fontainebleau, France},
              series    = {Lecture Notes in Computer Science},
              volume    = {10867},
              publisher = {Springer},
              address   = {Cham, Switzerland},
              year      = {2018},
              abstract  = {Lorem Ipsum}
            }
        """,

        "valid phdthesis" to """
            @phdthesis{CitekeyPhdthesis,
              author  = {Rempel, Robert Charles},
              title   = {Relaxation Effects for Coupled Nuclear Spins},
              school  = {Stanford University},
              address = {Stanford, CA},
              year    = {1956},
              month   = {jun},
              abstract = {Lorem Ipsum}
            }
        """,

        "valid mastersthesis" to """
            @mastersthesis{CitekeyMastersthesis,
              author  = {Jian Tang},
              title   = {Spin structure of the nucleon in the asymptotic limit},
              school  = {Massachusetts Institute of Technology},
              year    = {1996},
              address = {Cambridge, MA},
              month   = {sep},
              abstract = {Lorem Ipsum}
            }
        """,

        "valid inbook" to """
            @inbook{CitekeyInbook,
              author    = {Lisa A. Urry and Michael L. Cain and Steven A. Wasserman and Peter V. Minorsky and Jane B. Reece},
              title     = {Photosynthesis},
              booktitle = {Campbell Biology},
              year      = {2016},
              publisher = {Pearson},
              address   = {New York, NY},
              pages     = {187--221},
              abstract  = {Lorem Ipsum}
            }
        """,

        "valid booklet" to """
            @booklet{CitekeyBooklet,
              title        = {Canoe tours in {S}weden},
              author       = {Maria Swetla}, 
              howpublished = {Distributed at the Stockholm Tourist Office},
              month        = {jul},
              year         = {2015},
              abstract     = {Lorem Ipsum}
            }
        """,

        "valid manual" to """
            @manual{CitekeyManual,
              title        = {{R}: A Language and Environment for Statistical Computing},
              author       = {{R Core Team}},
              organization = {R Foundation for Statistical Computing},
              address      = {Vienna, Austria},
              year         = {2018},
              abstract     = {Lorem Ipsum}
            }
        """,

        "valid misc" to """
            @misc{CitekeyMisc,
              title        = {Pluto: The 'Other' Red Planet},
              author       = {{NASA}},
              howpublished = {\url{https://www.nasa.gov/nh/pluto-the-other-red-planet}},
              year         = {2015},
              note         = {Accessed: 2018-12-06},
              abstract     = {Lorem Ipsum}
            }
        """,

        "valid unpublished" to """
            @unpublished{CitekeyUnpublished,
              author = {Mohinder Suresh},
              title  = {Evolution: a revised theory},
              year   = {2006},
              journal = {Lorem Ipsum},
              abstract = {Lorem Ipsum}
            }
        """,

        "multiple bibtex entries" to """
            @article{nash51,
                title = {Non-cooperative Games},
                year = {1951},
                author = {Nash, John},
                journal = {Annals of Mathematics},
                abstract = {Lorem Ipsum},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
                doi = {10.1234/doi}
            }
            
            @INPROCEEDINGS{Rodrigues11MOSA,
              author    = {Onofre {Trindade Júnior}},
              title     = {{Using SOA in Critical-Embedded Systems}},
              booktitle = {Proceedings of the 4${'$'}^{th}${'$'}  IEEE (CPSCom'11)},
              year      = {2011},
              pages     = {733-738},
              address   = {Dalian, China},
              abstract  = {Lorem ipsum},
              references = {ref3, ref4},
              doi        = {10.1021/ci025584y}
            }
            
            @Techreport{Durelli2011LPSRM,
                author    = {Rafael Serapilha Durelli},
                title     = {Uma abordagem apoiada por linguagens específicas de domínio para a criação de linhas de produto de software embarcado},
                institution  = {Universidade Federal de São Carlos (UFSCar)},
                year      = {2011},
                type      = {Dissertação de Mestrado},
                address   = {São Carlos, SP},
                abstract  = {Lorem Ipsum}
            }
            
            @BOOK{Bass03SAPRr,
              title      = {Software Architecture in Practice},
              publisher  = {Addison-Wesley},
              year       = {2012},
              series     = {SEI Series in Software Engineering},
              edition    = {3},
              author     = {Len Bass and Paul Clements and Rick Kazman},
              abstract    = {Lorem Ipsum}
            }
            
            @proceedings{CitekeyProceedings,
              editor    = {Susan Stepney and Sergey Verlan},
              title     = {Proceedings of the 17th International Conference on Computation and Natural Computation, Fontainebleau, France},
              series    = {Lecture Notes in Computer Science},
              volume    = {10867},
              publisher = {Springer},
              address   = {Cham, Switzerland},
              year      = {2018},
              abstract  = {Lorem Ipsum}
            }
            
            @phdthesis{CitekeyPhdthesis,
              author  = {Rempel, Robert Charles},
              title   = {Relaxation Effects for Coupled Nuclear Spins},
              school  = {Stanford University},
              address = {Stanford, CA},
              year    = {1956},
              month   = {jun}
              abstract = {Lorem Ipsum}
            }
            
            @mastersthesis{CitekeyMastersthesis,
              author  = {Jian Tang},
              title   = {Spin structure of the nucleon in the asymptotic limit},
              school  = {Massachusetts Institute of Technology},
              year    = {1996},
              address = {Cambridge, MA},
              month   = {sep}
              abstract = {Lorem Ipsum}
            }
        """,

        "multiple bibtex entries with some invalid" to """
            //invalid
            @article{nash51,
                title = {Non-cooperative Games},
                abstract = {Lorem Ipsum},
                keywords = {keyword1, keyword2},
                references = {ref1, ref2},
                doi = {10.1234/doi}
            }
            //removed {}
            @INPROCEEDINGS{Rodrigues11MOSA,
              author    = {Onofre {Trindade Júnior}},
              title     = {{Using SOA in Critical-Embedded Systems}},
              booktitle = {Proceedings of the 4${'$'}^{th}${'$'}  IEEE (CPSCom'11)},
              year      = {2011},
              pages     = {733-738},
              address   = {Dalian, China},
              abstract  = {Lorem ipsum},
              references = {ref3, ref4},
              doi        = {10.1021/ci025584y}
            }
            
            @Techreport{Durelli2011LPSRM,
                author    = {Rafael Serapilha Durelli},
                title     = {Uma abordagem apoiada por linguagens específicas de domínio para a criação de linhas de produto de software embarcado},
                institution  = {Universidade Federal de São Carlos (UFSCar)},
                year      = {2011},
                type      = {Dissertação de Mestrado},
                address   = {São Carlos, SP},
                abstract  = {Lorem Ipsum}
            }
            
            @BOOK{Bass03SAPRr,
              title      = {Software Architecture in Practice},
              publisher  = {Addison-Wesley},
              year       = {2012},
              series     = {SEI Series in Software Engineering},
              edition    = {3},
              author     = {Len Bass and Paul Clements and Rick Kazman},
              abstract    = {Lorem Ipsum}
            }
            //removed type
            {CitekeyProceedings,
              editor    = {Susan Stepney and Sergey Verlan},
              title     = {Proceedings of the 17th International Conference on Computation and Natural Computation, Fontainebleau, France},
              series    = {Lecture Notes in Computer Science},
              volume    = {10867},
              publisher = {Springer},
              address   = {Cham, Switzerland},
              year      = {2018},
              abstract  = {Lorem Ipsum}
            }
            //removed school
            @phdthesis{CitekeyPhdthesis,
              author  = {Rempel, Robert Charles},
              title   = {Relaxation Effects for Coupled Nuclear Spins},
              address = {Stanford, CA},
              year    = {1956},
              month   = {jun}
              abstract = {Lorem Ipsum}
            }
            //removed @
            mastersthesis{CitekeyMastersthesis,
              author  = {Jian Tang},
              title   = {Spin structure of the nucleon in the asymptotic limit},
              school  = {Massachusetts Institute of Technology},
              year    = {1996},
              address = {Cambridge, MA},
              month   = {sep}
              abstract = {Lorem Ipsum}
            }
        """
    )
}