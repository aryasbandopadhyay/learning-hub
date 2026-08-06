"""Runnable demo.

Run from python/:  python -m vending.main
"""

from __future__ import annotations

from .machine import VendingMachine


def main() -> None:
    machine = VendingMachine.demo_machine()

    print(
        "Stock at open: "
        f"WATER={machine.stock_of('WATER')}, "
        f"CHIPS={machine.stock_of('CHIPS')}, "
        f"SODA={machine.stock_of('SODA')}"
    )

    machine.insert_money(25)
    water = machine.select_product("WATER")
    print(water.message)
    print("WATER stock now:", machine.stock_of("WATER"))

    machine.insert_money(25)
    chips = machine.select_product("CHIPS")
    print(chips.message)

    machine.insert_money(10)
    machine.insert_money(5)
    refund = machine.cancel()
    print(refund.message)
    print("State now:", machine.current_state().name)


if __name__ == "__main__":
    main()
