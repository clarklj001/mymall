package de.killbuqs.common.valid;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {

	private Set<Integer> set = new HashSet<>();

	@Override
	public void initialize(ListValue constraintAnnotation) {
		int[] values = constraintAnnotation.values();
		for (int value : values) {
			set.add(value);
		}
	}

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		return set.contains(value);

	}

}
