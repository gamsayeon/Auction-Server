package com.ccommit.auction_server.repository;

import com.ccommit.auction_server.model.ProductComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCommentRepository extends JpaRepository<ProductComment, Long> {

}
