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

import io.limbo.cqrs.core.command.ICommand;
import io.limbo.cqrs.core.command.VoidCommand;
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

	@Test
	void testSubTypesOf_shouldFindImplementationsInSamePackage() {
		// Configure to scan the test package
		ReflectionUtils.configure("io.limbo");

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
	void testRefType_withVoidCommand_shouldExtractVoidType() {
		// Test VoidCommand-like scenario: interface extends interface with generic type
		// This simulates: class MyCmd implements VoidCommand extends ICommand<Void>
		SimulatedVoidCommand command = new SimulatedVoidCommand() {};

		Class<Void> resultType = ReflectionUtils.refType(command);

		assertEquals(Void.class, resultType);
	}

	@Test
	void testRefType_withDeeplyInheritedInterface_shouldExtractGenericType() {
		// Test deeply nested interface inheritance (3 levels)
		// SimulatedDeepCommand -> SimulatedMiddleCommand<String> -> SimulatedICommand<String>
		SimulatedDeepCommand command = new SimulatedDeepCommand() {};

		Class<String> resultType = ReflectionUtils.refType(command);

		assertEquals(String.class, resultType);
	}

	// Simulates ICommand<R> from CQRS module
	interface SimulatedICommand<R> {
	}

	// Simulates VoidCommand extends ICommand<Void>
	interface SimulatedVoidCommand extends SimulatedICommand<Void> {
	}

	// Simulates a middle interface that redefines the generic type
	interface SimulatedMiddleCommand<R> extends SimulatedICommand<R> {
	}

	// Simulates a command with generic type specified at middle level
	interface SimulatedDeepCommand extends SimulatedMiddleCommand<String> {
	}

	// ========== Tests using actual CQRS classes ==========

	@Test
	void testRefType_withActualVoidCommand_shouldExtractVoidType() {
		// Test actual VoidCommand from CQRS module
		// VoidCommand extends ICommand<Void>
		VoidCommand command = new ActualVoidCommandImpl() {};

		Class<Void> resultType = ReflectionUtils.refType(command);

		assertEquals(Void.class, resultType);
	}

	@Test
	void testRefType_withActualICommand_shouldExtractGenericType() {
		// Test actual ICommand with String type
		ICommand<String> command = new ActualStringCommand() {};

		Class<String> resultType = ReflectionUtils.refType(command);

		assertEquals(String.class, resultType);
	}

	@Test
	void testRefType_withActualIntegerCommand_shouldExtractIntegerType() {
		// Test actual ICommand with Integer type
		ICommand<Integer> command = new ActualIntegerCommand() {};

		Class<Integer> resultType = ReflectionUtils.refType(command);

		assertEquals(Integer.class, resultType);
	}

	@Test
	void testRefType_withConcreteVoidCommand_shouldExtractVoidType() {
		// Test concrete custom command that implements VoidCommand
		CreateUserCommand command = new CreateUserCommand("test@example.com");

		Class<Void> resultType = ReflectionUtils.refType(command);

		assertEquals(Void.class, resultType);
	}

	// Concrete implementation of VoidCommand for testing
	abstract static class ActualVoidCommandImpl implements VoidCommand {
	}

	// Concrete implementation of ICommand<String> for testing
	abstract static class ActualStringCommand implements ICommand<String> {
	}

	// Concrete implementation of ICommand<Integer> for testing
	abstract static class ActualIntegerCommand implements ICommand<Integer> {
	}

	// A real-world-like concrete command that implements VoidCommand
	static class CreateUserCommand implements VoidCommand {
		private final String email;

		CreateUserCommand(String email) {
			this.email = email;
		}

		String getEmail() {
			return email;
		}
	}
}
