export type PriceRange = 'CHEAP' | 'MODERATE' | 'EXPENSIVE' | 'LUXURY';

export interface SalonSummary {
  id: number;
  name: string;
  district: string;
  address: string;
  rating: number | null;
  reviewCount: number | null;
  priceRange: PriceRange | null;
  servicesOffered: string[];
}

export interface SalonDetail {
  id: number;
  name: string;
  address: string;
  district: string;
  phoneNumber: string | null;
  website: string | null;
  servicesOffered: string[];
  priceRange: PriceRange | null;
  rating: number | null;
  reviewCount: number | null;
  description: string | null;
  latitude: number | null;
  longitude: number | null;
  createdAt: string;
  updatedAt: string;
}

export interface SalonRequest {
  name: string;
  address: string;
  district: string;
  phoneNumber: string | null;
  website: string | null;
  servicesOffered: string[];
  priceRange: PriceRange | null;
  rating: number | null;
  reviewCount: number | null;
  description: string | null;
  latitude: number | null;
  longitude: number | null;
}
