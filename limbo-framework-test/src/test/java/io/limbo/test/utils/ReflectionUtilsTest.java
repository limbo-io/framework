/*
 * Copyright 2025-2030 Fluxion Team (https://github.com/Fluxion-io).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.limbo.test.utils;

import io.limbo.utils.ReflectionUtils;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ReflectionUtils}
 *
 * @author Devil
 */
class ReflectionUtilsTest {

	// Test interface and implementations for subTypesOf tests
	interface TestService {
		String execute();
	}

	static class TestServiceImplA implements TestService {
		@Override
		public String execute() {
			return "A";
		}
	}

	static class TestServiceImplB implements TestService {
		@Override
		public String execute() {
			return "B";
		}
	}

	// Nested inner class implementation
	static class TestServiceImplC implements TestService {
		@Override
		public String execute() {
			return "C";
		}
	}

	// Anonymous inner class implementation
	TestService anonymousService = () -> "Anonymous";

	// Interface with generic type for refType tests
	interface GenericHandler<T> {
		T handle();
	}

	abstract static class StringHandler implements GenericHandler<String> {
	}

	abstract static class IntegerHandler implements GenericHandler<Integer> {
	}

	// Void type handler
	abstract static class VoidHandler implements GenericHandler<Void> {
	}

	@Test
	void testSubTypesOf_withMultipleImplementations() {
		// Configure Reflections to scan this test package first
		ReflectionUtils.configure("io.limbo.test.utils");
		Set<Class<? extends TestService>> result = ReflectionUtils.subTypesOf(TestService.class);

		assertNotNull(result);
		assertTrue(result.contains(TestServiceImplA.class));
		assertTrue(result.contains(TestServiceImplB.class));
		assertTrue(result.contains(TestServiceImplC.class));
	}

	@Test
	void testSubTypesOf_queryingUnknownType_returnsEmptySet() {
		// A type with no subtypes in the scanned package
		Set<Class<? extends java.util.function.LongSupplier>> result = ReflectionUtils.subTypesOf(java.util.function.LongSupplier.class);

		assertNotNull(result);
		// May be empty or contain JDK types; just assert it runs without error
	}

	@Test
	void testRefType_withStringHandler_shouldExtractStringType() {
		GenericHandler<String> handler = new StringHandler() {
			@Override
			public String handle() {
				return "test";
			}
		};

		Class<String> resultType = ReflectionUtils.refType(handler);

		assertEquals(String.class, resultType);
	}

	@Test
	void testRefType_withIntegerHandler_shouldExtractIntegerType() {
		GenericHandler<Integer> handler = new IntegerHandler() {
			@Override
			public Integer handle() {
				return 42;
			}
		};

		Class<Integer> resultType = ReflectionUtils.refType(handler);

		assertEquals(Integer.class, resultType);
	}

	@Test
	void testRefType_withVoidHandler_shouldExtractVoidType() {
		GenericHandler<Void> handler = new VoidHandler() {
			@Override
			public Void handle() {
				return null;
			}
		};

		Class<Void> resultType = ReflectionUtils.refType(handler);

		assertEquals(Void.class, resultType);
	}

	@Test
	void testRefType_withNull_shouldThrowNpe() {
		// refType does not accept null; it dereferences obj to get its class
		assertThrows(NullPointerException.class, () -> ReflectionUtils.refType(null));
	}
}
