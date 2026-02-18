package com.labb.vishinandroid.domain.model;

public class DecisionTree {
    public static double[] score(double[] input) {
        double[] leafProbability;
        if (input[128] <= 0.09816407039761543) {
            if (input[851] <= 0.061218008399009705) {
                if (input[284] <= 0.12234743684530258) {
                    if (input[298] <= 0.1473962515592575) {
                        if (input[954] <= 0.07549833506345749) {
                            if (input[31] <= 0.3894089162349701) {
                                if (input[161] <= 0.0865425392985344) {
                                    if (input[343] <= 0.1473923772573471) {
                                        if (input[10] <= 0.08802684396505356) {
                                            if (input[731] <= 0.11512813717126846) {
                                                leafProbability = new double[] {0.9634426571965741, 0.03655734280342594};
                                            } else {
                                                leafProbability = new double[] {0.0, 1.0};
                                            }
                                        } else {
                                            leafProbability = new double[] {0.0, 1.0};
                                        }
                                    } else {
                                        if (input[236] <= 0.4895774871110916) {
                                            if (input[397] <= 0.22789829969406128) {
                                                leafProbability = new double[] {0.07142857142857142, 0.9285714285714286};
                                            } else {
                                                leafProbability = new double[] {1.0, 0.0};
                                            }
                                        } else {
                                            if (input[645] <= 0.2499752789735794) {
                                                leafProbability = new double[] {1.0, 0.0};
                                            } else {
                                                leafProbability = new double[] {0.0, 1.0};
                                            }
                                        }
                                    }
                                } else {
                                    leafProbability = new double[] {0.0, 1.0};
                                }
                            } else {
                                if (input[53] <= 0.061076365411281586) {
                                    if (input[414] <= 0.27829018235206604) {
                                        if (input[455] <= 0.2756047248840332) {
                                            leafProbability = new double[] {0.0, 1.0};
                                        } else {
                                            leafProbability = new double[] {1.0, 0.0};
                                        }
                                    } else {
                                        leafProbability = new double[] {1.0, 0.0};
                                    }
                                } else {
                                    leafProbability = new double[] {1.0, 0.0};
                                }
                            }
                        } else {
                            leafProbability = new double[] {0.0, 1.0};
                        }
                    } else {
                        if (input[294] <= 0.4903522878885269) {
                            if (input[236] <= 0.4280255138874054) {
                                if (input[645] <= 0.459690198302269) {
                                    if (input[791] <= 0.5142824351787567) {
                                        if (input[405] <= 0.6662682294845581) {
                                            if (input[868] <= 0.2308635711669922) {
                                                leafProbability = new double[] {0.06451612903225806, 0.9354838709677419};
                                            } else {
                                                leafProbability = new double[] {1.0, 0.0};
                                            }
                                        } else {
                                            leafProbability = new double[] {1.0, 0.0};
                                        }
                                    } else {
                                        leafProbability = new double[] {1.0, 0.0};
                                    }
                                } else {
                                    if (input[247] <= 0.26640060544013977) {
                                        leafProbability = new double[] {1.0, 0.0};
                                    } else {
                                        leafProbability = new double[] {0.0, 1.0};
                                    }
                                }
                            } else {
                                leafProbability = new double[] {1.0, 0.0};
                            }
                        } else {
                            leafProbability = new double[] {1.0, 0.0};
                        }
                    }
                } else {
                    if (input[123] <= 0.06226544454693794) {
                        if (input[529] <= 0.059968240559101105) {
                            if (input[432] <= 0.09463664144277573) {
                                if (input[492] <= 0.12081088125705719) {
                                    if (input[814] <= 0.12643812596797943) {
                                        if (input[140] <= 0.07287406921386719) {
                                            if (input[174] <= 0.22271385788917542) {
                                                leafProbability = new double[] {0.08080808080808081, 0.9191919191919192};
                                            } else {
                                                leafProbability = new double[] {1.0, 0.0};
                                            }
                                        } else {
                                            if (input[522] <= 0.141063392162323) {
                                                leafProbability = new double[] {1.0, 0.0};
                                            } else {
                                                leafProbability = new double[] {0.0, 1.0};
                                            }
                                        }
                                    } else {
                                        leafProbability = new double[] {1.0, 0.0};
                                    }
                                } else {
                                    leafProbability = new double[] {1.0, 0.0};
                                }
                            } else {
                                leafProbability = new double[] {1.0, 0.0};
                            }
                        } else {
                            leafProbability = new double[] {1.0, 0.0};
                        }
                    } else {
                        leafProbability = new double[] {1.0, 0.0};
                    }
                }
            } else {
                if (input[795] <= 0.03168262541294098) {
                    if (input[730] <= 0.09377133101224899) {
                        if (input[344] <= 0.1539013534784317) {
                            if (input[498] <= 0.23385870456695557) {
                                leafProbability = new double[] {1.0, 0.0};
                            } else {
                                leafProbability = new double[] {0.0, 1.0};
                            }
                        } else {
                            leafProbability = new double[] {0.0, 1.0};
                        }
                    } else {
                        leafProbability = new double[] {0.0, 1.0};
                    }
                } else {
                    if (input[185] <= 0.09870664775371552) {
                        if (input[681] <= 0.1865188404917717) {
                            leafProbability = new double[] {0.0, 1.0};
                        } else {
                            leafProbability = new double[] {1.0, 0.0};
                        }
                    } else {
                        leafProbability = new double[] {1.0, 0.0};
                    }
                }
            }
        } else {
            if (input[128] <= 0.18110457807779312) {
                if (input[492] <= 0.056299660354852676) {
                    if (input[529] <= 0.06361202150583267) {
                        if (input[310] <= 0.18192851543426514) {
                            if (input[537] <= 0.1568378284573555) {
                                if (input[685] <= 0.0952187031507492) {
                                    if (input[572] <= 0.08838284015655518) {
                                        if (input[382] <= 0.172764353454113) {
                                            if (input[838] <= 0.10708313435316086) {
                                                leafProbability = new double[] {0.007692307692307693, 0.9923076923076923};
                                            } else {
                                                leafProbability = new double[] {1.0, 0.0};
                                            }
                                        } else {
                                            leafProbability = new double[] {1.0, 0.0};
                                        }
                                    } else {
                                        leafProbability = new double[] {1.0, 0.0};
                                    }
                                } else {
                                    leafProbability = new double[] {1.0, 0.0};
                                }
                            } else {
                                leafProbability = new double[] {1.0, 0.0};
                            }
                        } else {
                            leafProbability = new double[] {1.0, 0.0};
                        }
                    } else {
                        if (input[992] <= 0.1274118721485138) {
                            leafProbability = new double[] {1.0, 0.0};
                        } else {
                            leafProbability = new double[] {0.0, 1.0};
                        }
                    }
                } else {
                    if (input[157] <= 0.12593722343444824) {
                        if (input[933] <= 0.15578316897153854) {
                            leafProbability = new double[] {1.0, 0.0};
                        } else {
                            leafProbability = new double[] {0.0, 1.0};
                        }
                    } else {
                        leafProbability = new double[] {0.0, 1.0};
                    }
                }
            } else {
                if (input[560] <= 0.06439884752035141) {
                    if (input[977] <= 0.13388365507125854) {
                        if (input[289] <= 0.08581481128931046) {
                            if (input[749] <= 0.14289717376232147) {
                                if (input[589] <= 0.1700102537870407) {
                                    if (input[500] <= 0.17052263021469116) {
                                        if (input[800] <= 0.16938652098178864) {
                                            if (input[186] <= 0.200781911611557) {
                                                leafProbability = new double[] {0.9925925925925926, 0.007407407407407408};
                                            } else {
                                                leafProbability = new double[] {0.0, 1.0};
                                            }
                                        } else {
                                            leafProbability = new double[] {0.0, 1.0};
                                        }
                                    } else {
                                        leafProbability = new double[] {0.0, 1.0};
                                    }
                                } else {
                                    if (input[943] <= 0.19239991903305054) {
                                        leafProbability = new double[] {0.0, 1.0};
                                    } else {
                                        leafProbability = new double[] {1.0, 0.0};
                                    }
                                }
                            } else {
                                leafProbability = new double[] {0.0, 1.0};
                            }
                        } else {
                            if (input[289] <= 0.22006934136152267) {
                                leafProbability = new double[] {0.0, 1.0};
                            } else {
                                if (input[126] <= 0.09784924983978271) {
                                    leafProbability = new double[] {1.0, 0.0};
                                } else {
                                    leafProbability = new double[] {0.0, 1.0};
                                }
                            }
                        }
                    } else {
                        leafProbability = new double[] {0.0, 1.0};
                    }
                } else {
                    if (input[560] <= 0.20443078130483627) {
                        if (input[492] <= 0.08728203177452087) {
                            if (input[397] <= 0.08030974119901657) {
                                leafProbability = new double[] {0.0, 1.0};
                            } else {
                                leafProbability = new double[] {1.0, 0.0};
                            }
                        } else {
                            leafProbability = new double[] {1.0, 0.0};
                        }
                    } else {
                        if (input[348] <= 0.10266575962305069) {
                            if (input[289] <= 0.13479050993919373) {
                                if (input[859] <= 0.187345951795578) {
                                    if (input[560] <= 0.6226646602153778) {
                                        leafProbability = new double[] {1.0, 0.0};
                                    } else {
                                        leafProbability = new double[] {0.0, 1.0};
                                    }
                                } else {
                                    leafProbability = new double[] {0.0, 1.0};
                                }
                            } else {
                                leafProbability = new double[] {0.0, 1.0};
                            }
                        } else {
                            leafProbability = new double[] {0.0, 1.0};
                        }
                    }
                }
            }
        }
        return leafProbability;
    }
}