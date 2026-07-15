package br.all.application.report.export

data class  ReviewExportData(
    val systematicStudy: SystematicReviewExportData,
    val protocol: ProtocolExportData,
    val studiesIncludedInScreening: List<StudyExportData>,
    val studiesExcludedInScreening: List<StudyExportData>,
    val includedStudies: List<StudyExportData>,
    val studiesExcludedInFullText: List<StudyExportData>,
    val funnel: FunnelExportData
)