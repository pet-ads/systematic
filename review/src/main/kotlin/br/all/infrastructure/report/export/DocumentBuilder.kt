package br.all.infrastructure.report.export

import br.all.application.report.export.FunnelExportData
import br.all.application.report.export.ProtocolExportData
import br.all.application.report.export.StudyExportData
import br.all.application.report.export.SystematicReviewExportData

interface DocumentBuilder<T> {
    fun addSystematicStudy(data: SystematicReviewExportData): DocumentBuilder<T>
    fun addProtocol(data: ProtocolExportData): DocumentBuilder<T>
    fun addFunnel(data: FunnelExportData): DocumentBuilder<T>
    fun addStudies(
        includedInScreening: List<StudyExportData>,
        excludedInScreening: List<StudyExportData>,
        included: List<StudyExportData>,
        excludedInFullText: List<StudyExportData>,
    ): DocumentBuilder<T>
    fun build(): T
}
