package com.ccommit.auction_server.elasticsearchRepository;

import com.ccommit.auction_server.model.ELK.DocumentBid;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidSelectRepository extends ElasticsearchRepository<DocumentBid, String> {
    List<DocumentBid> findByBuyerId(Long buyerId);
    List<DocumentBid> findByBuyerIdAndProductId(Long buyerId, Long productId);

    List<DocumentBid> findByProductId(Long productId);

}
