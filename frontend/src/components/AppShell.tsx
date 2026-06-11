"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { ReactNode } from "react";

const navItems = [
  {
    href: "/dashboard",
    label: "Dashboard",
  },
  {
    href: "/deployments",
    label: "Deployments",
  },
  {
    href: "/deployments/new",
    label: "Create Deployment",
  },
  {
    href: "/integrations",
    label: "Integrations",
  },
];

export function AppShell({ children }: { children: ReactNode }) {
  const pathname = usePathname();

  function isActive(href: string) {
    if (href === "/deployments") {
      return (
        pathname === href ||
        (pathname.startsWith("/deployments/") &&
          !pathname.startsWith("/deployments/new"))
      );
    }

    return pathname === href;
  }

  function navClassName(href: string) {
    const active = isActive(href);

    return [
      "block rounded-lg px-3 py-2 text-sm font-medium",
      active
        ? "bg-slate-900 text-white"
        : "text-slate-700 hover:bg-slate-100",
    ].join(" ");
  }

  return (
    <div className="min-h-screen bg-slate-50">
      <aside className="fixed left-0 top-0 hidden h-screen w-64 border-r border-slate-200 bg-white p-6 md:block">
        <h1 className="text-xl font-bold text-slate-900">ReleasePilot</h1>
        <p className="mt-1 text-sm text-slate-500">
          Deployment intelligence
        </p>

        <nav className="mt-8 space-y-2">
          {navItems.map((item) => (
            <Link
              key={item.href}
              href={item.href}
              className={navClassName(item.href)}
            >
              {item.label}
            </Link>
          ))}
        </nav>
      </aside>

      <header className="border-b border-slate-200 bg-white p-4 md:hidden">
        <h1 className="text-lg font-bold text-slate-900">ReleasePilot</h1>
        <nav className="mt-4 grid grid-cols-2 gap-2">
          {navItems.map((item) => (
            <Link
              key={item.href}
              href={item.href}
              className={navClassName(item.href)}
            >
              {item.label}
            </Link>
          ))}
        </nav>
      </header>

      <main className="md:pl-64">
        <div className="mx-auto max-w-7xl p-6">{children}</div>
      </main>
    </div>
  );
}
