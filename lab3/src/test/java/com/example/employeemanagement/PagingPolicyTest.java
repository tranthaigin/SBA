package com.example.employeemanagement;

import com.example.employeemanagement.exceptions.ApiException;
import com.example.employeemanagement.services.PagingPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.Sort;
import static org.assertj.core.api.Assertions.*;

class PagingPolicyTest {
    @Test void defaults_sortIdAscending() {
        assertThat(PagingPolicy.create(0, 10, null).getSort().toList()).containsExactly(Sort.Order.asc("empId"));
    }
    @ParameterizedTest @ValueSource(ints = {-1, 0, 101, 100000})
    void invalidSize_returnsBadRequest(int size) {
        assertThatThrownBy(() -> PagingPolicy.create(0, size, null)).isInstanceOf(ApiException.class);
    }
    @Test void negativePage_returnsBadRequest() {
        assertThatThrownBy(() -> PagingPolicy.create(-1, 10, null)).isInstanceOf(ApiException.class);
    }
    @ParameterizedTest @ValueSource(strings = {"password,asc", "empName,wrong", "empName,asc,desc", "", "empName,"})
    void invalidSort_returnsBadRequest(String expression) {
        assertThatThrownBy(() -> PagingPolicy.create(0, 10, new String[]{expression})).isInstanceOf(ApiException.class);
    }
    @Test void duplicateSortField_returnsBadRequest() {
        assertThatThrownBy(() -> PagingPolicy.create(0, 10, new String[]{"empName,asc", "empName,desc"}))
                .isInstanceOf(ApiException.class);
    }
    @Test void largestPage_usesLongOffsetWithoutOverflow() {
        assertThat(PagingPolicy.create(Integer.MAX_VALUE, 100, null).getOffset()).isEqualTo(214748364700L);
    }
}
