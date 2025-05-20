package br.all.domain.services

object ReviewSimilarityTestData {
    val testInputs = mapOf(
        "high similarity" to """
            @article{original,
            title = {Machine Learning Applications in Healthcare},
            year = {2023},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.001}
            }

            @article{duplicate,
            title = {Machine Learning Applications in Healthcare Systems},
            year = {2023},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            abstract = {This paper explores various applications of machine learning in healthcare, with emphasis on diagnosis and treatment optimization.},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.001}
            }
        """,

        "identical content" to """
            @article{original,
            title = {Machine Learning Applications in Healthcare},
            year = {2023},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.001}
            }

            @article{duplicate,
            title = {Machine Learning Applications in Healthcare},
            year = {2023},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.001}
            }
        """,

        "below threshold" to """
            @article{paper1,
            title = {Machine Learning Applications in Healthcare},
            year = {2023},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.001}
            }

            @article{paper2,
            title = {Deep Learning for Medical Image Analysis},
            year = {2023},
            author = {Robert Johnson and Sarah Williams},
            journal = {Journal of Medical Imaging},
            abstract = {This study presents a comprehensive review of deep learning techniques applied to medical image analysis, with a focus on MRI and CT scans.},
            keywords = {deep learning, medical imaging, MRI},
            doi = {10.1234/jmi.2023.002}
            }
        """,

        "null abstracts" to """
            @article{original,
            title = {Machine Learning Applications in Healthcare},
            year = {2023},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.001}
            }

            @article{duplicate,
            title = {Machine Learning Applications in Healthcare},
            year = {2023},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.001}
            }
        """,

        "single study" to """
            @article{single,
            title = {Machine Learning Applications in Healthcare},
            year = {2023},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.001}
            }
        """,

        "multiple groups" to """
            @article{group1_original,
            title = {Machine Learning Applications in Healthcare},
            year = {2023},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.001}
            }

            @article{group1_duplicate,
            title = {Machine Learning Applications in Healthcare},
            year = {2023},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.001}
            }

            @article{group2_original,
            title = {Deep Learning for Medical Image Analysis},
            year = {2023},
            author = {Robert Johnson and Sarah Williams},
            journal = {Journal of Medical Imaging},
            abstract = {This study presents a comprehensive review of deep learning techniques applied to medical image analysis, with a focus on MRI and CT scans.},
            keywords = {deep learning, medical imaging, MRI},
            doi = {10.1234/jmi.2023.002}
            }

            @article{group2_duplicate,
            title = {Deep Learning for Medical Image Analysis},
            year = {2023},
            author = {Robert Johnson and Sarah Williams},
            journal = {Journal of Medical Imaging},
            abstract = {This study presents a comprehensive review of deep learning techniques applied to medical image analysis, with a focus on MRI and CT scans.},
            keywords = {deep learning, medical imaging, MRI},
            doi = {10.1234/jmi.2023.002}
            }

            @article{unique,
            title = {Natural Language Processing in Clinical Settings},
            year = {2023},
            author = {David Brown and Lisa Chen},
            journal = {Journal of Clinical Informatics},
            abstract = {This article discusses the implementation of natural language processing techniques in clinical settings for improving patient care.},
            keywords = {NLP, clinical informatics, patient care},
            doi = {10.1234/jci.2023.003}
            }
        """,

        "different years" to """
            @article{original,
            title = {Machine Learning Applications in Healthcare},
            year = {2023},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.001}
            }

            @article{duplicate,
            title = {Extension study of: Machine Learning Applications in Healthcare Systems},
            year = {2024},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            abstract = {This paper is an updated version of an old study that explores various applications of machine learning in healthcare, with emphasis on diagnosis and treatment optimization.},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.001}
            }
        """,

        "multiple combinations" to """
            @article{group1_original,
            title = {Machine Learning Applications in Healthcare},
            year = {2023},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.001}
            }

            @article{group1_similar_title,
            title = {Machine Learning Applications in Healthcare Systems},
            year = {2023},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.002}
            }

            @article{group1_similar_abstract,
            title = {Machine Learning Applications in Healthcare},
            year = {2023},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            abstract = {This paper explores various applications of ML in healthcare, with focus on diagnosis and treatment optimization approaches.},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.003}
            }

            @article{group1_different_year,
            title = {Machine Learning Applications in Healthcare},
            year = {2022},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2022.001}
            }

            @article{group1_different_authors,
            title = {Machine Learning Applications in Healthcare},
            year = {2023},
            author = {Robert Johnson and Sarah Williams},
            journal = {Journal of Medical Informatics},
            abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.004}
            }

            @article{group1_different_abstract,
            title = {Machine Learning Applications in Healthcare},
            year = {2023},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            abstract = {This study presents a completely different approach to healthcare analytics using statistical methods and traditional algorithms.},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.005}
            }

            @article{group2_original,
            title = {Deep Learning for Medical Image Analysis},
            year = {2023},
            author = {Robert Johnson and Sarah Williams},
            journal = {Journal of Medical Imaging},
            abstract = {This study presents a comprehensive review of deep learning techniques applied to medical image analysis, with a focus on MRI and CT scans.},
            keywords = {deep learning, medical imaging, MRI},
            doi = {10.1234/jmi.2023.006}
            }

            @article{group2_duplicate,
            title = {Deep Learning for Medical Image Analysis},
            year = {2023},
            author = {Robert Johnson and Sarah Williams},
            journal = {Journal of Medical Imaging},
            abstract = {This study presents a comprehensive review of deep learning techniques applied to medical image analysis, with a focus on MRI and CT scans.},
            keywords = {deep learning, medical imaging, MRI},
            doi = {10.1234/jmi.2023.007}
            }

            @article{group2_similar_title_authors,
            title = {Deep Learning Approaches for Medical Image Analysis},
            year = {2023},
            author = {Robert Johnson and Sarah Williams},
            journal = {Journal of Medical Imaging},
            abstract = {A completely different abstract about something else entirely to test threshold behavior.},
            keywords = {deep learning, medical imaging, MRI},
            doi = {10.1234/jmi.2023.008}
            }

            @article{group3_no_abstract1,
            title = {Natural Language Processing in Clinical Settings},
            year = {2023},
            author = {David Brown and Lisa Chen},
            journal = {Journal of Clinical Informatics},
            keywords = {NLP, clinical informatics, patient care},
            doi = {10.1234/jci.2023.001}
            }

            @article{group3_no_abstract2,
            title = {Natural Language Processing in Clinical Settings},
            year = {2023},
            author = {David Brown and Lisa Chen},
            journal = {Journal of Clinical Informatics},
            keywords = {NLP, clinical informatics, patient care},
            doi = {10.1234/jci.2023.002}
            }

            @article{unique1,
            title = {Blockchain Applications in Healthcare Data Management},
            year = {2023},
            author = {Michael Lee and Jennifer Wang},
            journal = {Journal of Health Informatics},
            abstract = {This paper discusses the potential of blockchain technology for secure and efficient healthcare data management.},
            keywords = {blockchain, healthcare, data management},
            doi = {10.1234/jhi.2023.001}
            }

            @article{unique2,
            title = {Internet of Things in Remote Patient Monitoring},
            year = {2023},
            author = {Thomas Wilson and Emily Davis},
            journal = {Journal of Telemedicine},
            abstract = {This research explores the applications of IoT devices in remote patient monitoring systems.},
            keywords = {IoT, remote monitoring, telemedicine},
            doi = {10.1234/jt.2023.001}
            }
        """,

        "different sources" to """
            @article{original,
            title = {Machine Learning Applications in Healthcare},
            year = {2023},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.001}
            }

            @article{duplicate,
            title = {Machine Learning Applications in Healthcare},
            year = {2023},
            author = {John Smith and Maria Garcia},
            journal = {Journal of Medical Informatics},
            abstract = {This paper explores various applications of machine learning in healthcare, focusing on diagnosis and treatment optimization.},
            keywords = {machine learning, healthcare, diagnosis},
            doi = {10.1234/jmi.2023.001}
            }
        """
    )
}