package com.biniyogbuddy.market.mapper;

import com.biniyogbuddy.market.dto.MarketSummaryResponse;
import com.biniyogbuddy.market.entity.MarketSnapshot;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MarketSnapshotMapper {

    MarketSummaryResponse toSummaryResponse(MarketSnapshot snapshot);
}
