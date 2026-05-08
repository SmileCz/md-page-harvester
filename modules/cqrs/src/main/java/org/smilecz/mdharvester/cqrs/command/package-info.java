/**
 * CQRS command side contracts.
 *
 * <p>Commands change state and do not return application data. This root package contains the
 * application-facing command contracts. Pipeline implementation details live in responsibility
 * focused subpackages such as {@code bus}, {@code store}, {@code validation}, {@code authorization},
 * {@code retry}, {@code transaction}, {@code audit}, {@code error}, and {@code idempotency}.</p>
 */
package org.smilecz.mdharvester.cqrs.command;
