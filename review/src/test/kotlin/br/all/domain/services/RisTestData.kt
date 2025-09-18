package br.all.domain.services

object RisTestData {
    val testInput = mapOf(
        "valid RIS entrie" to """TY  - JOUR
        TI  - Sampling for Scalable Visual Analytics
        T2  - IEEE Computer Graphics and Applications
        SP  - 100
        EP  - 108
        AU  - B. C. Kwon
        AU  - P. J. Haas
        PY  - 2017
        KW  - Temperature sensors
        KW  - Data visualization
        DO  - 10.1109/MCG.2017.6
        JO  - IEEE Computer Graphics and Applications
        IS  - 1
        SN  - 1558-1756
        VO  - 37
        VL  - 37
        JA  - IEEE Computer Graphics and Applications
        Y1  - Jan.-Feb. 2017
        AB  - Lorem Ipsum
        ER  - 
        """,
        "multiple RIS entries" to """TY  - CHAP
        TI  - TITLE1
        T2  - TITLE12
        SP  - 1
        EP  - 10
        AU  - K. Shvachko
        PY  - 2010
        KW  - File systems
        DO  - 10.1109/MSST
        JO  - 2010 IEEE 26th Symposium on Mass Storage Systems and Technologies (MSST)
        IS  -
        SN  - 2160-1968
        VO  -
        VL  -
        JA  - 2010 IEEE 26th Symposium on Mass Storage Systems and Technologies (MSST)
        Y1  - 3-7 May 2010
        AB  - ABSTRACT1 
        ER  -
        
        TY  - CONF
        TI  - TITLE2
        T2  - TITLE22
        SP  - 250
        EP  - 257
        AU  - V. Kalavri
        PY  - 2013
        KW  - Accuracy
        KW  - Estimation
        DO  - 10.1109/CloudCom.2013.40
        JO  - 2013 IEEE
        IS  -
        SN  -
        VO  - 1
        VL  - 1
        JA  - 2013 IEEE
        Y1  - 2-5 Dec. 2013
        AB  - ABSTRACT2
        ER  -
        
        TY  - ABST
        TI  - TITLE3
        T2  - TITLE33
        SP  - 163
        EP  - 168
        AU  - S. Phansalkar
        PY  - 2016
        KW  - Partitioning algorithms
        DO  - 10.1109/PDGC.2016.7913137
        JO  - 2016
        IS  -
        SN  -
        VO  -
        VL  -
        JA  - 2016 
        Y1  - 22-24 Dec. 2016
        AB  - ABSTRACT3 
        ER  -
        """,

        "valid inproceedings" to """TY  - CPAPER
        TI  - Muitos testes
        SP  - 100
        EP  - 108
        AU  - Gabriel
        AU  - Erick
        PY  - 2017
        KW  - Tes
        KW  - Tes
        DO  - 10.1109/MCG.2017.6
        JO  - Meu Computador
        IS  - 1
        SN  - 1558-1756
        VO  - 37
        VL  - 37
        JA  - IEEE Computer Graphics and Applications
        Y1  - Jan.-Feb. 2017
        AB  - Lorem Ipsum
        ER  - 
        """,
        "valid techreport" to """TY  - RPRT
        TI  - Uma abordagem apoiada por linguagens específicas de domínio
        T2  - para a criação de linhas de produto de software embarcado
        SP  - 100
        EP  - 108
        AU  - Rafael Serapilha Durelli
        PY  - 2011
        KW  - KW
        DO  - 10.1109/MCG.2017.6
        JO  - Universidade Federal de São Carlos (UFSCar)
        IS  - 1
        SN  - 1558-1756
        VO  - 37
        VL  - 37
        JA  - IEEE Computer Graphics and Applications
        Y1  - Jan.-Feb. 2017
        AB  - Lorem Ipsum
        ER  - 
        """,

        "valid book" to """TY  - BOOK
        TI  - Software Architecture in Practice
        SP  - 100
        EP  - 108
        AU  - Len Bass
        AU  - Paul Clements
        AU  - Rick Kazman
        PY  - 2012
        KW  - KW
        DO  - 10.1109/MCG.2017.6
        JO  - Addison-Wesley
        IS  - 1
        SN  - 1558-1756
        VO  - 37
        VL  - 37
        JA  - IEEE Computer Graphics and Applications
        Y1  - Jan.-Feb. 2017
        AB  - Lorem Ipsum
        ER  - 
        """,

        "valid proceedings" to """TY  - CONF
        TI  - Proceedings of the 17th International Conference on Computation
        T2  - and Natural Computation, Fontainebleau, France
        SP  - 100
        EP  - 108
        AU  - Susan Stepney
        PY  - 2018
        KW  - KW
        DO  - 10.1109/MCG.2017.6
        JO  - Springer
        IS  - 1
        SN  - 1558-1756
        VO  - 37
        VL  - 37
        JA  - IEEE Computer Graphics and Applications
        Y1  - Jan.-Feb. 2017
        AB  - Lorem Ipsum
        ER  - 
        """,

        "valid masterthesis" to """TY  - THES
        TI  - Relaxation Effects for Coupled Nuclear Spins
        SP  - 100
        EP  - 108
        AU  - Rempel
        AU  - Robert Charles
        PY  - 1956
        KW  - KW
        DO  - 10.1109/MCG.2017.6
        JO  - Stanford University
        IS  - 1
        SN  - 1558-1756
        VO  - 37
        VL  - 37
        JA  - IEEE Computer Graphics and Applications
        Y1  - Jan.-Feb. 2017
        AB  - Lorem Ipsum
        ER  - 
        """,

        "valid inbook" to """TY  - ECHAP
        TI  - Relaxation Effects for Coupled Nuclear Spins
        SP  - 100
        EP  - 108
        AU  - Rempel
        AU  - Robert Charles
        PY  - 1956
        KW  - KW
        DO  - 10.1109/MCG.2017.6
        JO  - Stanford University
        IS  - 1
        SN  - 1558-1756
        VO  - 37
        VL  - 37
        JA  - IEEE Computer Graphics and Applications
        Y1  - Jan.-Feb. 2017
        AB  - Lorem Ipsum
        ER  - 
        """,

        "valid booklet" to """TY  - PAMP
        TI  - Relaxation Effects for Coupled Nuclear Spins
        SP  - 100
        EP  - 108
        AU  - Rempel
        AU  - Robert Charles
        PY  - 1956
        KW  - KW
        DO  - 10.1109/MCG.2017.6
        JO  - Stanford University
        IS  - 1
        SN  - 1558-1756
        VO  - 37
        VL  - 37
        JA  - IEEE Computer Graphics and Applications
        Y1  - Jan.-Feb. 2017
        AB  - Lorem Ipsum
        ER  - 
        """,

        "valid misc" to """TY  - VIDEO
        TI  - Pluto: The 'Other' Red Planet
        AU  - NASA
        PY  - 2015
        KW  - KW
        DO  - 10.1109/MCG.2017.6
        JO  - \url{https://www.nasa.gov/nh/pluto-the-other-red-planet}
        IS  - 1
        Y1  - Jan.-Feb. 2017
        AB  - Lorem Ipsum
        ER  - 
        """,

        "multiple A1" to """TY  - JOUR
        TI  - Sampling for Scalable Visual Analytics
        T2  - IEEE Computer Graphics and Applications
        SP  - 100
        EP  - 108
        A1  - B. C. Kwon
        A1  - P. J. Haas
        PY  - 2017
        KW  - Temperature sensors
        KW  - Data visualization
        DO  - 10.1109/MCG.2017.6
        JO  - IEEE Computer Graphics and Applications
        IS  - 1
        SN  - 1558-1756
        VO  - 37
        VL  - 37
        JA  - IEEE Computer Graphics and Applications
        Y1  - Jan.-Feb. 2017
        AB  - Lorem Ipsum
        ER  - 
        """,

        "T1 test" to """TY  - JOUR
        T1  - Sampling for Scalable Visual Analytics
        T2  - IEEE Computer Graphics and Applications
        SP  - 100
        EP  - 108
        AU  - B. C. Kwon
        AU  - P. J. Haas
        PY  - 2017
        KW  - Temperature sensors
        KW  - Data visualization
        DO  - 10.1109/MCG.2017.6
        JO  - IEEE Computer Graphics and Applications
        IS  - 1
        SN  - 1558-1756
        VO  - 37
        VL  - 37
        JA  - IEEE Computer Graphics and Applications
        Y1  - Jan.-Feb. 2017
        AB  - Lorem Ipsum
        ER  - 
        """,

        "Y1 test" to """TY  - JOUR
        TI  - Sampling for Scalable Visual Analytics
        T2  - IEEE Computer Graphics and Applications
        SP  - 100
        EP  - 108
        AU  - B. C. Kwon
        AU  - P. J. Haas
        KW  - Temperature sensors
        KW  - Data visualization
        DO  - 10.1109/MCG.2017.6
        JO  - IEEE Computer Graphics and Applications
        IS  - 1
        SN  - 1558-1756
        VO  - 37
        VL  - 37
        JA  - IEEE Computer Graphics and Applications
        Y1  - Jan.-Feb. 2017
        AB  - Lorem Ipsum
        ER  - 
        """,

        "unknown ris" to """TY  - SALAME
        TI  - Pluto: The 'Other' Red Planet
        AU  - NASA
        PY  - 2015
        KW  - KW
        DO  - 10.1109/MCG.2017.6
        JO  - \url{https://www.nasa.gov/nh/pluto-the-other-red-planet}
        IS  - 1
        Y1  - Jan.-Feb. 2017
        AB  - Lorem Ipsum
        ER  - 
        """,

        "invalid title" to """TY  - JOUR
        AU  - NASA
        PY  - 2015
        KW  - KW
        DO  - 10.1109/MCG.2017.6
        JO  - \url{https://www.nasa.gov/nh/pluto-the-other-red-planet}
        IS  - 1
        Y1  - Jan.-Feb. 2017
        AB  - Lorem Ipsum
        ER  - 
        """,

        "invalid authors" to """TY  - JOUR
        PY  - 2015
        KW  - KW
        DO  - 10.1109/MCG.2017.6
        JO  - \url{https://www.nasa.gov/nh/pluto-the-other-red-planet}
        IS  - 1
        Y1  - Jan.-Feb. 2017
        AB  - Lorem Ipsum
        ER  - 
        """,

        "invalid year" to """TY  - JOUR
        AU  - NASA
        KW  - KW
        DO  - 10.1109/MCG.2017.6
        JO  - \url{https://www.nasa.gov/nh/pluto-the-other-red-planet}
        IS  - 1
        Y1  - blablabla
        AB  - Lorem Ipsum
        ER  - 
        """,

        "invalid venue" to """TY  - JOUR
        AU  - NASA
        PY  - 2015
        KW  - KW
        DO  - 10.1109/MCG.2017.6
        IS  - 1
        Y1  - Jan.-Feb. 2017
        AB  - Lorem Ipsum
        ER  - 
        """,

        "invalid abstract" to """TY  - JOUR
        AU  - NASA
        PY  - 2015
        KW  - KW
        DO  - 10.1109/MCG.2017.6
        JO  - \url{https://www.nasa.gov/nh/pluto-the-other-red-planet}
        IS  - 1
        Y1  - Jan.-Feb. 2017
        ER  - 
        """,

        "invalid doi" to """TY  - VIDEO
        TI  - Pluto: The 'Other' Red Planet
        AU  - NASA
        PY  - 2015
        KW  - KW
        DO  - 
        JO  - \url{https://www.nasa.gov/nh/pluto-the-other-red-planet}
        IS  - 1
        Y1  - Jan.-Feb. 2017
        AB  - Lorem Ipsum
        ER  - 
        """,

        "three error ris" to """
        TY  - JOUR
TI  - Sampling for Scalable Visual Analytics
T2  - IEEE Computer Graphics and Applications
SP  - 100
EP  - 108
AU  - B. C. Kwon
AU  - P. J. Haas
PY  - 2017
KW  - Temperature sensors
KW  - Data visualization
DO  - 10.1109/MCG.2017.6
JO  - IEEE Computer Graphics and Applications
IS  - 1
SN  - 1558-1756
VL  - 37
JA  - IEEE Computer Graphics and Applications
Y1  - 2017-01
AB  - Lorem Ipsum
ER  -

TY  - CHAP
TI  - TITLE12
SP  - 1
EP  - 10
AU  - K. Shvachko
PY  - 2010
KW  - File systems
DO  - 10.1109/MSST
JO  - 2010 IEEE 26th Symposium on Mass Storage Systems and Technologies (MSST)
SN  - 2160-1968
Y1  - 2010-05
AB  - ABSTRACT1
ER  -

TY  - CONF
TI  - TITLE2
SP  - 250
EP  - 257
PY  - 2013
JA  - 2013 IEEE
Y1  - 2013-12
AB  - ABSTRACT2
ER  -

TY  - CONF
TI  - TITLE
KW  - Partitioning algorithms
DO  - 10.1109/PDGC.2016.7913137
Y1  - 2016-12
AB  - ABSTRACT3
ER  -

TY  - JOUR
T2  - IEEE Computer Graphics and Applications
SP  - 100
EP  - 108
AU  - B. C. Kwon
PY  - 2017
KW  - Temperature sensors
KW  - Data visualization
DO  - 10.1109/MCG.2017.6
IS  - 1
SN  - 1558-1756
VL  - 37
Y1  - 2017-01
ER  -
        """
    )
}