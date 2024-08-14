### RpaSign classes

- [ ] **Task**: Improve code comprehension and maintainability.
    - **Action**: Add docstrings to all RpaSign classes.

### RpaDescRule class

- [X] **Task**: Manage parking scenarios.
    - **Action**: Introduced a variable into the RpaDescRule class to differentiate between Parking and Non-Parking
      scenarios.

### Implement `JsonSerializable`

- [ ] **Task**: Make the classes that use `toJson` to extend `JsonSerializable`.

### Change 'WEEKLY_DAYS' to 'DAY_OF_WEEK'

- [ ] **Task**: Rename the constant 'WEEKLY_DAYS' to 'DAY_OF_WEEK'.
  - **Action**: Simplify and avoid confusion by matching the constant name with its purpose.

### Sign Verification

- [ ] **Task**: Verify parsing of 'DAY-DAY'.
    - **Action**: For sign SB-TU-LM, check if the 'DAY-DAY' is correctly parsed.
    - **Example of signs (code)** : SB-TU-LM, SB-TU-MJ, SB-TU-MV
- [ ] 