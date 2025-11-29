package ru.ssau.tk._shederu_._lab1_.search;

public class SearchCriteriaBuilder {
    private FunctionSearchSystem.SearchCriteria criteria;

    public SearchCriteriaBuilder() {
        this.criteria = new FunctionSearchSystem.SearchCriteria();
    }

    public static SearchCriteriaBuilder create() {
        return new SearchCriteriaBuilder();
    }

    public SearchCriteriaBuilder withExpression(String pattern) {
        criteria.withExpressionPattern(pattern);
        return this;
    }

    public SearchCriteriaBuilder withName(String pattern) {
        criteria.withNamePattern(pattern);
        return this;
    }

    public SearchCriteriaBuilder withLogin(String pattern) {
        criteria.withLoginPattern(pattern);
        return this;
    }

    public SearchCriteriaBuilder withUserId(Long userId) {
        criteria.withUserId(userId);
        return this;
    }

    public SearchCriteriaBuilder withCompositeFunctions() {
        criteria.withTargetType(ru.ssau.tk._shederu_._lab1_.entities.CompositeFunctionEntity.class);
        return this;
    }

    public SearchCriteriaBuilder withTabulatedFunctions() {
        criteria.withTargetType(ru.ssau.tk._shederu_._lab1_.entities.TabulatedFunctionEntity.class);
        return this;
    }

    public SearchCriteriaBuilder withUsers() {
        criteria.withTargetType(ru.ssau.tk._shederu_._lab1_.entities.UserEntity.class);
        return this;
    }

    public SearchCriteriaBuilder withAllTypes() {
        criteria.withAllTargetTypes();
        return this;
    }

    public FunctionSearchSystem.SearchCriteria build() {
        return criteria;
    }
}