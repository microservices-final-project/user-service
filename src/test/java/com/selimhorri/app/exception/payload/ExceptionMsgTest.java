package com.selimhorri.app.exception.payload;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZonedDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class ExceptionMsgTest {
    
    private ZonedDateTime timestamp;
    private Throwable throwable;
    private HttpStatus httpStatus;
    private String msg;
    
    @BeforeEach
    void setUp() {
        timestamp = ZonedDateTime.now(ZoneId.systemDefault());
        throwable = new RuntimeException("Test exception");
        httpStatus = HttpStatus.BAD_REQUEST;
        msg = "Test error message";
    }
    
    @Test
    void testConstructorWithAllParameters() {
        // Given & When
        ExceptionMsg exceptionMsg = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        
        // Then
        assertEquals(timestamp, exceptionMsg.getTimestamp());
        assertEquals(throwable, exceptionMsg.getThrowable());
        assertEquals(httpStatus, exceptionMsg.getHttpStatus());
        assertEquals(msg, exceptionMsg.getMsg());
    }
    
    @Test
    void testConstructorWithoutThrowable() {
        // Given & When
        ExceptionMsg exceptionMsg = new ExceptionMsg(timestamp, httpStatus, msg);
        
        // Then
        assertEquals(timestamp, exceptionMsg.getTimestamp());
        assertNull(exceptionMsg.getThrowable());
        assertEquals(httpStatus, exceptionMsg.getHttpStatus());
        assertEquals(msg, exceptionMsg.getMsg());
    }
    
    @Test
    void testBuilder() {
        // Given & When
        ExceptionMsg exceptionMsg = ExceptionMsg.builder()
                .timestamp(timestamp)
                .throwable(throwable)
                .httpStatus(httpStatus)
                .msg(msg)
                .build();
        
        // Then
        assertEquals(timestamp, exceptionMsg.getTimestamp());
        assertEquals(throwable, exceptionMsg.getThrowable());
        assertEquals(httpStatus, exceptionMsg.getHttpStatus());
        assertEquals(msg, exceptionMsg.getMsg());
    }
    
    @Test
    void testBuilderWithoutThrowable() {
        // Given & When
        ExceptionMsg exceptionMsg = ExceptionMsg.builder()
                .timestamp(timestamp)
                .httpStatus(httpStatus)
                .msg(msg)
                .build();
        
        // Then
        assertEquals(timestamp, exceptionMsg.getTimestamp());
        assertNull(exceptionMsg.getThrowable());
        assertEquals(httpStatus, exceptionMsg.getHttpStatus());
        assertEquals(msg, exceptionMsg.getMsg());
    }
    
    @Test
    void testGetTimestamp() {
        // Given
        ExceptionMsg exceptionMsg = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        
        // When
        ZonedDateTime result = exceptionMsg.getTimestamp();
        
        // Then
        assertEquals(timestamp, result);
    }
    
    @Test
    void testGetThrowable() {
        // Given
        ExceptionMsg exceptionMsg = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        
        // When
        Throwable result = exceptionMsg.getThrowable();
        
        // Then
        assertEquals(throwable, result);
    }
    
    @Test
    void testGetThrowableWhenNull() {
        // Given
        ExceptionMsg exceptionMsg = new ExceptionMsg(timestamp, httpStatus, msg);
        
        // When
        Throwable result = exceptionMsg.getThrowable();
        
        // Then
        assertNull(result);
    }
    
    @Test
    void testGetHttpStatus() {
        // Given
        ExceptionMsg exceptionMsg = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        
        // When
        HttpStatus result = exceptionMsg.getHttpStatus();
        
        // Then
        assertEquals(httpStatus, result);
    }
    
    @Test
    void testGetMsg() {
        // Given
        ExceptionMsg exceptionMsg = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        
        // When
        String result = exceptionMsg.getMsg();
        
        // Then
        assertEquals(msg, result);
    }
    
    @Test
    void testSetThrowable() {
        // Given
        ExceptionMsg exceptionMsg = new ExceptionMsg(timestamp, httpStatus, msg);
        RuntimeException newThrowable = new RuntimeException("New exception");
        
        // When
        exceptionMsg.setThrowable(newThrowable);
        
        // Then
        assertEquals(newThrowable, exceptionMsg.getThrowable());
    }
    
    @Test
    void testSetThrowableToNull() {
        // Given
        ExceptionMsg exceptionMsg = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        
        // When
        exceptionMsg.setThrowable(null);
        
        // Then
        assertNull(exceptionMsg.getThrowable());
    }
    
    @Test
    void testEquals_SameObject() {
        // Given
        ExceptionMsg exceptionMsg = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        
        // When & Then
        assertEquals(exceptionMsg, exceptionMsg);
    }
    
    @Test
    void testEquals_EqualObjects() {
        // Given
        ExceptionMsg exceptionMsg1 = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        ExceptionMsg exceptionMsg2 = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        
        // When & Then
        assertEquals(exceptionMsg1, exceptionMsg2);
        assertEquals(exceptionMsg2, exceptionMsg1);
    }
    
    @Test
    void testEquals_DifferentTimestamp() {
        // Given
        ZonedDateTime differentTimestamp = timestamp.plusMinutes(1);
        ExceptionMsg exceptionMsg1 = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        ExceptionMsg exceptionMsg2 = new ExceptionMsg(differentTimestamp, throwable, httpStatus, msg);
        
        // When & Then
        assertNotEquals(exceptionMsg1, exceptionMsg2);
    }
    
    @Test
    void testEquals_DifferentThrowable() {
        // Given
        Throwable differentThrowable = new IllegalArgumentException("Different exception");
        ExceptionMsg exceptionMsg1 = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        ExceptionMsg exceptionMsg2 = new ExceptionMsg(timestamp, differentThrowable, httpStatus, msg);
        
        // When & Then
        assertNotEquals(exceptionMsg1, exceptionMsg2);
    }
    
    @Test
    void testEquals_DifferentHttpStatus() {
        // Given
        ExceptionMsg exceptionMsg1 = new ExceptionMsg(timestamp, throwable, HttpStatus.BAD_REQUEST, msg);
        ExceptionMsg exceptionMsg2 = new ExceptionMsg(timestamp, throwable, HttpStatus.INTERNAL_SERVER_ERROR, msg);
        
        // When & Then
        assertNotEquals(exceptionMsg1, exceptionMsg2);
    }
    
    @Test
    void testEquals_DifferentMsg() {
        // Given
        ExceptionMsg exceptionMsg1 = new ExceptionMsg(timestamp, throwable, httpStatus, "Message 1");
        ExceptionMsg exceptionMsg2 = new ExceptionMsg(timestamp, throwable, httpStatus, "Message 2");
        
        // When & Then
        assertNotEquals(exceptionMsg1, exceptionMsg2);
    }
    
    @Test
    void testEquals_NullObject() {
        // Given
        ExceptionMsg exceptionMsg = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        
        // When & Then
        assertNotEquals(exceptionMsg, null);
    }
    
    @Test
    void testEquals_DifferentClass() {
        // Given
        ExceptionMsg exceptionMsg = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        String differentObject = "Different class";
        
        // When & Then
        assertNotEquals(exceptionMsg, differentObject);
    }
    
    @Test
    void testEquals_WithNullThrowable() {
        // Given
        ExceptionMsg exceptionMsg1 = new ExceptionMsg(timestamp, null, httpStatus, msg);
        ExceptionMsg exceptionMsg2 = new ExceptionMsg(timestamp, null, httpStatus, msg);
        
        // When & Then
        assertEquals(exceptionMsg1, exceptionMsg2);
    }
    
    @Test
    void testEquals_OneNullThrowable() {
        // Given
        ExceptionMsg exceptionMsg1 = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        ExceptionMsg exceptionMsg2 = new ExceptionMsg(timestamp, null, httpStatus, msg);
        
        // When & Then
        assertNotEquals(exceptionMsg1, exceptionMsg2);
    }
    
    @Test
    void testHashCode_EqualObjects() {
        // Given
        ExceptionMsg exceptionMsg1 = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        ExceptionMsg exceptionMsg2 = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        
        // When & Then
        assertEquals(exceptionMsg1.hashCode(), exceptionMsg2.hashCode());
    }
    
    @Test
    void testHashCode_DifferentObjects() {
        // Given
        ExceptionMsg exceptionMsg1 = new ExceptionMsg(timestamp, throwable, httpStatus, "Message 1");
        ExceptionMsg exceptionMsg2 = new ExceptionMsg(timestamp, throwable, httpStatus, "Message 2");
        
        // When & Then
        assertNotEquals(exceptionMsg1.hashCode(), exceptionMsg2.hashCode());
    }
    
    @Test
    void testHashCode_WithNullThrowable() {
        // Given
        ExceptionMsg exceptionMsg1 = new ExceptionMsg(timestamp, null, httpStatus, msg);
        ExceptionMsg exceptionMsg2 = new ExceptionMsg(timestamp, null, httpStatus, msg);
        
        // When & Then
        assertEquals(exceptionMsg1.hashCode(), exceptionMsg2.hashCode());
    }
    
    @Test
    void testHashCode_Consistency() {
        // Given
        ExceptionMsg exceptionMsg = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        
        // When
        int hashCode1 = exceptionMsg.hashCode();
        int hashCode2 = exceptionMsg.hashCode();
        
        // Then
        assertEquals(hashCode1, hashCode2);
    }
    
    @Test
    void testToString() {
        // Given
        ExceptionMsg exceptionMsg = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        
        // When
        String result = exceptionMsg.toString();
        
        // Then
        assertNotNull(result);
        assertTrue(result.contains("ExceptionMsg"));
        assertTrue(result.contains(msg));
        assertTrue(result.contains(httpStatus.toString()));
    }
    
    @Test
    void testToString_WithNullThrowable() {
        // Given
        ExceptionMsg exceptionMsg = new ExceptionMsg(timestamp, null, httpStatus, msg);
        
        // When
        String result = exceptionMsg.toString();
        
        // Then
        assertNotNull(result);
        assertTrue(result.contains("ExceptionMsg"));
        assertTrue(result.contains(msg));
        assertTrue(result.contains(httpStatus.toString()));
    }
    
    @Test
    void testToString_ContainsAllFields() {
        // Given
        ExceptionMsg exceptionMsg = new ExceptionMsg(timestamp, throwable, httpStatus, msg);
        
        // When
        String result = exceptionMsg.toString();
        
        // Then
        assertNotNull(result);
        assertTrue(result.contains("timestamp"));
        assertTrue(result.contains("throwable"));
        assertTrue(result.contains("httpStatus"));
        assertTrue(result.contains("msg"));
    }
}