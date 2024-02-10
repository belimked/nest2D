package com.nestapp

import com.nestapp.dxf.reader.Entity
import com.nestapp.dxf.reader.Line
import com.nestapp.dxf.reader.LwPolyline
import com.nestapp.dxf.writter.parts.DXFEntity
import com.nestapp.dxf.writter.parts.DXFLWPolyline
import com.nestapp.dxf.writter.parts.DXFLine
import com.nestapp.dxf.RealPoint
import com.nestapp.nest.data.NestPath
import com.nestapp.nest.data.Placement
import java.util.Vector
import kotlin.math.cos
import kotlin.math.sin

data class DxfPart(
    val entities: List<Entity>,
    val nestPath: NestPath,
) {

    val bid: Int
        get() = nestPath.bid
}

data class DxfPartPlacement(
    val entities: List<Entity>,
    val nestPath: NestPath,
    val placement: Placement,
) {
    fun getDXFEntities(): List<DXFEntity> {
        return entities.map { entity ->
            when (entity) {
                is LwPolyline -> getDXFLWPolyline(entity)
                is Line -> getDXFLine(entity)
                else -> throw RuntimeException("Not support entity")
            }
        }
    }

    private fun getDXFLine(line: Line): DXFLine {
        val start = RealPoint(line.xStart, line.yStart)
        val end = RealPoint(line.xEnd, line.yEnd)
        return DXFLine(start.transform(placement), end.transform(placement))
    }

    private fun getDXFLWPolyline(lwPolyline: LwPolyline): DXFLWPolyline {
        val vertices = Vector<RealPoint>()

        lwPolyline.segments.forEach { segment: LwPolyline.LSegment ->
            vertices.add(
                RealPoint(segment.dx, segment.dy)
                    .transform(placement)
            )
        }

        return DXFLWPolyline(vertices.size, vertices, true)
    }
}
