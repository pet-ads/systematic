package br.all.domain.services

object ScoreTestData {
    val testInputs = mapOf(
        "max score" to """
            @article{base,
            title = {Crescimento da dengue em países tropicais},
            year = {2025},
            author = {Fulano de Tal},
            journal = {Universidade X},
            abstract = {O clima atual favorece a reprodução do mosquito Aedes aegypti, aumentando o crescimento da dengue em países tropicais.},
            keywords = {dengue, mosquito, países tropicais},
            doi = {10.1234/dengue.2025.001}
            }
        """,

        "zero score" to """
            @article{nomatch,
            title = {Tecnologias de computação quântica na medicina moderna},
            year = {2025},
            author = {Fulano de Tal},
            journal = {Universidade X},
            abstract = {Estudo de algoritmos de machine learning aplicados à genômica usando computação quântica},
            keywords = {computação quântica, machine learning, genômica},
            doi = {10.1234/dengue.2025.001}
            }
        """,

        "partial score" to """
            @article{partialmatch,
            title = {Dengue em regiões urbanas},
            year = {2025},
            author = {Fulano de Tal},
            journal = {Universidade X},
            abstract = {Análise de fatores sociais e ambientais que afetam a propagação da dengue.},
            keywords = {dengue, regiões urbanas, clima},
            doi = {10.1234/dengue.2025.001}
            }
        """,

        "null abstract" to """
            @article{nullabstract,
            title = {Crescimento da dengue em países tropicais},
            year = {2025},
            author = {Fulano de Tal},
            journal = {Universidade X},
            keywords = {dengue, mosquito, países tropicais},
            doi = {10.1234/dengue.2025.001}
            }
        """,

        "multiple entries" to """
            @article{base,
            title = {Crescimento da dengue em países tropicais},
            year = {2025},
            author = {Fulano de Tal},
            journal = {Universidade X},
            abstract = {O clima atual favorece a reprodução do mosquito Aedes aegypti, aumentando o crescimento da dengue em países tropicais.},
            keywords = {dengue, mosquito, países tropicais},
            doi = {10.1234/dengue.2025.001}
            }

            @article{nomatch,
            title = {Tecnologias de computação quântica na medicina moderna},
            year = {2025},
            author = {Fulano de Tal},
            journal = {Universidade X},
            abstract = {Estudo de algoritmos de machine learning aplicados à genômica usando computação quântica},
            keywords = {computação quântica, machine learning, genômica},
            doi = {10.1234/dengue.2025.001}
            }

            @article{partialmatch,
            title = {Dengue em regiões urbanas},
            year = {2025},
            author = {Fulano de Tal},
            journal = {Universidade X},
            abstract = {Análise de fatores sociais e ambientais que afetam a propagação da dengue.},
            keywords = {dengue, regiões urbanas, clima},
            doi = {10.1234/dengue.2025.001}
            }
        """,

        "multiple title occurrences" to """
            @article{base,
            title = {Dengue, crescimento da dengue dengue em países com dengue tropicais dengue},
            year = {2025},
            author = {Fulano de Tal},
            journal = {Universidade X},
            abstract = {Qualquer coisa.},
            keywords = {dengue, mosquito},
            doi = {10.1234/dengue.2025.001}
            }
        """,

        "multiple abstract occurrences" to """
            @article{base,
            title = {Crescimento da dengue em países tropicais},
            year = {2025},
            author = {Fulano de Tal},
            journal = {Universidade X},
            abstract = {Dengue, o clima atual favorece dengue, a reprodução do mosquito mosquito da dengue dengue Aedes aegypti, aumentando o crescimento da dengue em países tropicais dengue.},
            keywords = {dengue, mosquito},
            doi = {10.1234/dengue.2025.001}
            }
        """
    )
}