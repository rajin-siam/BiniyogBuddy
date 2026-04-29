package com.biniyogbuddy.market.mapper;

import com.biniyogbuddy.market.dto.MarketStatusResponse;
import com.biniyogbuddy.market.dto.MarketSummaryResponse;
import com.biniyogbuddy.market.entity.MarketSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MarketSnapshotMapper {

    MarketSummaryResponse toSummaryResponse(MarketSnapshot snapshot);

    @Mapping(target = "status", expression = "java(snapshot.getMarketStatus().name())")
    @Mapping(target = "lastUpdatedOn", source = "fetchedAt")
    MarketStatusResponse toStatusResponse(MarketSnapshot snapshot);

}
