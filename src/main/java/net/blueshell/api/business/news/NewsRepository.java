package net.blueshell.api.business.news;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends PagingAndSortingRepository<News, Long>{

    @Query(value = "select n from News n order by n.postedAt desc ")
    @Override
    Page<News> findAll(Pageable pageable);
}
