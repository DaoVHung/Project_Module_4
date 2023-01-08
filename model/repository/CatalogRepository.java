package ra.dev.model.repository;import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.data.jpa.repository.Query;import org.springframework.data.repository.query.Param;import org.springframework.stereotype.Repository;import ra.dev.model.entity.Catalog;import java.util.List;@Repositorypublic interface CatalogRepository extends JpaRepository<Catalog, Integer> {    @Query(value = "select o from Catalog o where o.catalogName like '%'||:name||'%'" + " or o.id like '%'||:name||'%'  ")    List<Catalog> searchByNameAAndCatalogID(@Param("name") String name);    @Query(value = "select o from Catalog o where o.catalogStatus = true ")    List<Catalog> getAllCatalogActive();}