import { clsx } from "clsx";
import { twMerge } from "tailwind-merge";

// Combines clsx (conditional classes) with twMerge (deduplicates conflicting Tailwind classes)
export function cn(...inputs) {
  return twMerge(clsx(inputs));
}
