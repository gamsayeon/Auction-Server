package com.ccommit.auction_server.mapper;

import com.ccommit.auction_server.dto.BidDTO;
import com.ccommit.auction_server.model.Bid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BidMapper {
    private final ModelMapper modelMapper;

    public Bid convertToEntity(BidDTO bidDTO, Long productId, Long buyerId) {
        Bid bid = Bid.builder()
                .buyerId(buyerId)
                .productId(productId)
                .bidTime(LocalDateTime.now())
                .price(bidDTO.getPrice())
                .build();
        return bid;
    }

    public BidDTO convertToDTO(Bid bid) {
        BidDTO bidDTO = modelMapper.map(bid, BidDTO.class);
        return bidDTO;
    }

    public List<BidDTO> convertToDTOList(List<Bid> bids) {
        List<BidDTO> bidDTOs = new ArrayList<>();
        for (Bid bid : bids) {
            bidDTOs.add(modelMapper.map(bid, BidDTO.class));
        }
        return bidDTOs;
    }
}
