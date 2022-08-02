package me.lozm.global.querydsl;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@Repository
public abstract class Querydsl4RepositorySupport<T> {

    private final Class<T> domainClass;
    private Querydsl querydsl;
    private EntityManager entityManager;
    private JPAQueryFactory queryFactory;

    protected Querydsl4RepositorySupport(Class<T> domainClass) {
        Assert.notNull(domainClass, "Domain class must not be null!");

        this.domainClass = domainClass;
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        Assert.notNull(entityManager, "EntityManager must not be null!");

        JpaEntityInformation<T, ?> jpaEntityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager);
        SimpleEntityPathResolver resolver = SimpleEntityPathResolver.INSTANCE;
        EntityPath<T> entityPath = resolver.createPath(jpaEntityInformation.getJavaType());
        this.entityManager = entityManager;
        this.querydsl = new Querydsl(entityManager, new PathBuilder<>(entityPath.getType(), entityPath.getMetadata()));
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @PostConstruct
    public void validate() {
        Assert.notNull(entityManager, "EntityManager must not be null!");
        Assert.notNull(querydsl, "Querydsl must not be null!");
        Assert.notNull(queryFactory, "QueryFactory must not be null!");
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected Querydsl getQuerydsl() {
        return querydsl;
    }

    protected JPAQueryFactory getQueryFactory() {
        return queryFactory;
    }

    protected <R> JPAQuery<R> select(Expression<R> expr) {
        return getQueryFactory().select(expr);
    }

    protected <R> JPAQuery<R> selectFrom(EntityPath<R> from) {
        return getQueryFactory().selectFrom(from);
    }

    protected <R> Page<R> applyPagination(Pageable pageable, Function<JPAQueryFactory, JPAQuery> contentQuery) {
        JPAQuery jpaQuery = contentQuery.apply(getQueryFactory());
        List<R> content = getQuerydsl().applyPagination(pageable, jpaQuery).fetch();
        return PageableExecutionUtils.getPage(content, pageable, jpaQuery::fetchCount);
    }

    protected <R> Page<R> applyPagination(Pageable pageable, Function<JPAQueryFactory, JPAQuery> contentQuery, Function<JPAQueryFactory, JPAQuery> countQuery) {
        JPAQuery jpaContentQuery = contentQuery.apply(getQueryFactory());
        List<R> content = getQuerydsl().applyPagination(pageable, jpaContentQuery).fetch();
        JPAQuery countResult = countQuery.apply(getQueryFactory());
        return PageableExecutionUtils.getPage(content, pageable, countResult::fetchCount);
    }

    protected BooleanExpression isEqualTo(StringPath entityPathBase, String data) {
        return entityPathBase.eq(data);
    }

    protected BooleanExpression isEqualTo(NumberPath entityPathBase, Number data) {
        return entityPathBase.eq(data);
    }

    protected BooleanExpression isIncludedIn(StringPath entityPathBase, Collection data) {
        return entityPathBase.in(data);
    }

    protected <U extends Comparable> BooleanExpression isBetween(DateTimePath<U> dateTimePath, @Nullable U from, @Nullable U to) {
        return dateTimePath.between(from, to);
    }

}
