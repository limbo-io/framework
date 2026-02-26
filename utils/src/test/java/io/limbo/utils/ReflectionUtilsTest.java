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

package io.limbo.utils;

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

	@Test
	void testSubTypesOf_shouldFindImplementationsInSamePackage() {
		// Configure to scan the test package
		ReflectionUtils.configure("io.limbo.utils");

		// Test finding subtypes of TestService
		Set<Class<? extends TestService>> subTypes = ReflectionUtils.subTypesOf(TestService.class);

		// Should find all three implementations
		assertNotNull(subTypes);
		assertTrue(subTypes.size() >= 3, "Should find at least 3 implementations of TestService");
		assertTrue(subTypes.contains(TestServiceImplA.class));
		assertTrue(subTypes.contains(TestServiceImplB.class));
		assertTrue(subTypes.contains(TestServiceImplC.class));
	}

	@Test
	void testConfigure_withEmptyPackages_shouldNotThrow() {
		// Should not throw when passing empty array
		assertDoesNotThrow(() -> ReflectionUtils.configure());
		assertDoesNotThrow(() -> ReflectionUtils.configure((String[]) null));
	}

	@Test
	void testConfigure_withNullReflections_shouldNotThrow() {
		// Should not throw when passing null
		assertDoesNotThrow(() -> ReflectionUtils.configure((org.reflections.Reflections) null));
	}

	@Test
	void testRefType_shouldExtractGenericType() {
		// Create an anonymous implementation of a generic interface
		TestGenericCommand<String> command = new TestGenericCommand<String>() {};

		// refType should extract the generic type parameter (String)
		Class<String> resultType = ReflectionUtils.refType(command);

		assertEquals(String.class, resultType);
	}

	@Test
	void testRefType_withIntegerGeneric_shouldExtractCorrectType() {
		// Create an anonymous implementation with Integer generic type
		TestGenericCommand<Integer> command = new TestGenericCommand<Integer>() {};

		Class<Integer> resultType = ReflectionUtils.refType(command);

		assertEquals(Integer.class, resultType);
	}

	// Generic interface for testing refType
	interface TestGenericCommand<R> {
	}
}
